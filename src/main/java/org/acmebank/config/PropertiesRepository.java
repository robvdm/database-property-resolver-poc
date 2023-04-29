package org.acmebank.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;

public class PropertiesRepository implements AutoCloseable {
    private static final Logger log = Logger.getLogger(PropertiesRepository.class);

    private static final String QUERY_FORMAT = 
            "SELECT application, \"key\", value, encrypted FROM properties WHERE application = ? " +
            "UNION " +
            "SELECT application, \"key\", value, encrypted FROM properties WHERE application = 'general' " +
                "AND \"key\" NOT IN (SELECT \"key\" FROM properties WHERE application = ?)";

    private static final String VALUE_QUERY_FORMAT =
            "SELECT application, \"key\", value, encrypted FROM properties WHERE application = ? " +
            " AND \"key\" = ? " +
            "UNION " +
            "SELECT application, \"key\", value, encrypted FROM properties WHERE application = 'general' " +
                "AND \"key\" NOT IN (SELECT \"key\" FROM properties WHERE application = ?)" +
            " AND \"key\" = ? ";

    private AgroalDataSource dataSource;
    
    private final String applicationName;

    public PropertiesRepository(DatabaseConfigConfig config) throws SQLException {
        prepareDataSource(config);
        this.applicationName = config.applicationName();
    }

    public synchronized Map<String, String> getAllConfigValues() {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement selectAllStmt = connection.prepareStatement(QUERY_FORMAT)) {
            selectAllStmt.setString(1, applicationName);
            selectAllStmt.setString(2, applicationName);
            try (ResultSet rs = selectAllStmt.executeQuery()) {
                final Map<String, String> result = new HashMap<>();
                while (rs.next()) {
                    result.put(rs.getString(2), decryptIfRequired(rs.getString(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.trace("config-jdbc: could not get values: " + e.getLocalizedMessage());
            return Collections.emptyMap();
        }
    }

    public synchronized Set<String> getPropertyNames() {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement selectKeysStmt = connection.prepareStatement(QUERY_FORMAT)) {
            selectKeysStmt.setString(1, applicationName);
            selectKeysStmt.setString(2, applicationName);
            try (ResultSet rs = selectKeysStmt.executeQuery()) {
                final Set<String> keys = new HashSet<>();
                while (rs.next()) {
                    keys.add(rs.getString(2));
                }
                return keys;
            }
        } catch (SQLException e) {
            log.trace("config-jdbc: could not get keys: " + e.getLocalizedMessage());
            return Collections.emptySet();
        }
    }

    public String getValue(String propertyName) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement selectValueStmt = connection.prepareStatement(VALUE_QUERY_FORMAT)) {
            selectValueStmt.setString(1, applicationName);
            selectValueStmt.setString(2, propertyName);
            selectValueStmt.setString(3, applicationName);
            selectValueStmt.setString(4, propertyName);
            try (ResultSet rs = selectValueStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(3);
                }
            }
        } catch (SQLException e) {
            log.error("config-jdbc: could not get value for key " + propertyName + ": " + e.getLocalizedMessage());
        }
        return null;
    }

    private void prepareDataSource(final DatabaseConfigConfig config) throws SQLException {
        // create supplier
        AgroalDataSourceConfigurationSupplier dataSourceConfiguration = new AgroalDataSourceConfigurationSupplier();
        // get reference to connection pool
        AgroalConnectionPoolConfigurationSupplier poolConfiguration = dataSourceConfiguration
                .connectionPoolConfiguration();
        // get reference to connection factory
        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfiguration = poolConfiguration
                .connectionFactoryConfiguration();

        // configure pool
        poolConfiguration
                .initialSize(config.initialSize())
                .minSize(config.minSize())
                .maxSize(config.maxSize())
                .acquisitionTimeout(config.acquisitionTimeout());

        // configure supplier
        connectionFactoryConfiguration
                .jdbcUrl(config.url())
                .credential(new NamePrincipal(config.username()))
                .credential(new SimplePassword(config.password()));

        dataSource = AgroalDataSource.from(dataSourceConfiguration.get());
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
    
    private String decryptIfRequired(String application, String key, String value, boolean encrypted) {
        // TODO use real decryption
        if (encrypted && value != null) {
            return value.substring("ENC(".length(), value.length() - 1);
        } else if (shouldPropertyBeEncrypted(key)) {
            log.warn(String.format("Property [{}].[{}] should be encrypted. Please make sure you follow the development and security guidelines.", application, key));
        }
        return value;
    }
    
    private boolean shouldPropertyBeEncrypted(String property) {
        return property.toLowerCase().contains("password") || property.toLowerCase().contains("secretkey");
    }


}