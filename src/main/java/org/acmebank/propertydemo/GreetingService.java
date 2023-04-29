package org.acmebank.propertydemo;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GreetingService {

    // just use a random property for a demo
    @ConfigProperty(name = "greeting", defaultValue = "Hello")
    String greeting; 

    public String greeting(String name) {
        return greeting + " " + name + "!\n";
    }

}