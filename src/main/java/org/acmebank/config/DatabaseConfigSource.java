package org.acmebank.config;

import java.util.Map;
import java.util.Set;

import io.smallrye.config.common.AbstractConfigSource;

public class DatabaseConfigSource extends AbstractConfigSource {

    private final PropertiesRepository repository;
    
    public DatabaseConfigSource(String name, PropertiesRepository repository, int defaultOrdinal) {
        super(name, defaultOrdinal);
        this.repository = repository;
    }

    @Override
    public Map<String, String> getProperties() {
        return repository.getAllConfigValues();
    }

    @Override
    public Set<String> getPropertyNames() {
        return repository.getPropertyNames();
    }

    @Override
    public String getValue(String propertyName) {
        return repository.getValue(propertyName);
    }
    @Override
    public int getOrdinal() {
        return 280;
    }
}