package com.telecom.ecloudframework.security.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "ecloud.security"
)
public class AbSecurityProperties {
    private String xssIngores = "";
    private String csrfIngores = "127.0.0.1";
    private String authIngores = "/login.*";
    private String ssoIngores = "";
    private boolean sessionRegistryBacked = false;
    private int maximumSessions = 1;

    public AbSecurityProperties() {
    }

    public String getXssIngores() {
        return this.xssIngores;
    }

    public void setXssIngores(String xssIngores) {
        this.xssIngores = xssIngores;
    }

    public String getCsrfIngores() {
        return this.csrfIngores;
    }

    public void setCsrfIngores(String csrfIngores) {
        this.csrfIngores = csrfIngores;
    }

    public String getAuthIngores() {
        return this.authIngores;
    }

    public void setAuthIngores(String authIngores) {
        this.authIngores = authIngores;
    }

    public String getSsoIngores() {
        return this.ssoIngores;
    }

    public void setSsoIngores(String ssoIngores) {
        this.ssoIngores = ssoIngores;
    }

    public boolean isSessionRegistryBacked() {
        return this.sessionRegistryBacked;
    }

    public void setSessionRegistryBacked(boolean sessionRegistryBacked) {
        this.sessionRegistryBacked = sessionRegistryBacked;
    }

    public int getMaximumSessions() {
        return this.maximumSessions;
    }

    public void setMaximumSessions(int maximumSessions) {
        this.maximumSessions = maximumSessions;
    }
}
