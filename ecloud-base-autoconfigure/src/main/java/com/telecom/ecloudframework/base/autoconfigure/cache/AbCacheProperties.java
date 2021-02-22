package com.telecom.ecloudframework.base.autoconfigure.cache;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "ecloud.cache"
)
public class AbCacheProperties {
    private AbCacheType type;

    public AbCacheProperties() {
    }

    public AbCacheType getType() {
        return this.type;
    }

    public void setType(AbCacheType type) {
        this.type = type;
    }
}
