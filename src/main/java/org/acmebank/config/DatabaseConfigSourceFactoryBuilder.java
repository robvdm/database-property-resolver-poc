package org.acmebank.config;

import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class DatabaseConfigSourceFactoryBuilder implements ConfigBuilder {
    
    @Override
    public SmallRyeConfigBuilder configBuilder(final SmallRyeConfigBuilder builder) {
        builder.withSources(new DatabaseConfigSourceFactory());
        return builder;
    }
}