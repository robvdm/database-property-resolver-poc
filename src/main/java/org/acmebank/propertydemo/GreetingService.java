package org.acmebank.propertydemo;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GreetingService {

    // just use a random property for a demo
    final String greeting;
    @ConfigProperty(name = "team", defaultValue = "Integration")
    String team;

    public GreetingService(@ConfigProperty(name = "greeting", defaultValue = "Hello") String greeting) {
        this.greeting = greeting;
    }

    public String greeting(String name) {
        return String.format("%s %s from team %s!%n", greeting, name, team);
    }

}