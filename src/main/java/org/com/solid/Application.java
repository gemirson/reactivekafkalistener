package org.com.solid;
/**
 * Example configuration class demonstrating Single Responsibility Principle.
 */
public class Application {
    private String gatewayUrl;

    public Application(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }
}
