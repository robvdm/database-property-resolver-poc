package org.acmebank.config;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.common.MapBackedConfigSource;

public class DatabaseConfigSourceFactory implements ConfigSourceFactory.ConfigurableConfigSourceFactory<DatabaseConfigConfig> {
    private static final Logger log = Logger.getLogger(DatabaseConfigSourceFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(final ConfigSourceContext context, final DatabaseConfigConfig config) {
        if (!config.enabled()) {
            return Collections.emptyList();
        }

        try {
            PropertiesRepository repository = new PropertiesRepository(config);
            if (config.cache()) {
                return Collections.singletonList(new InMemoryConfigSource("jdbc-config", repository.getAllConfigValues(), 280));
            } else {
                return Collections.singletonList(new DatabaseConfigSource("jdbc-config", repository, 280));
            }
        } catch (SQLException e) {
            log.warn("jdbc-config disabled. reason: " + e.getLocalizedMessage());
            return Collections.emptyList();
        }
    }

    private static final class InMemoryConfigSource extends MapBackedConfigSource {
        public InMemoryConfigSource(String name, Map<String, String> propertyMap, int defaultOrdinal) {
            super(name, propertyMap, defaultOrdinal);
        }
    }

    @Override
    public OptionalInt getPriority() {
        return OptionalInt.of(280);
    }
}