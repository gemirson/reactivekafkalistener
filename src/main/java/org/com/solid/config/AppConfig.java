package org.com.solid.config;

/**
 * Example configuration class demonstrating Single Responsibility Principle.
 */
public class AppConfig {
    private String gatewayUrl;

    public AppConfig(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }
}
