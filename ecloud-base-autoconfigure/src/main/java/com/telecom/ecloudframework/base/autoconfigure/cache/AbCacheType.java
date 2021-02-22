package com.telecom.ecloudframework.base.autoconfigure.cache;


public enum AbCacheType {
    MEMORY(AbMemoryCacheConfiguration.class),
    REDIS(AbRedisCacheConfiguration.class),
    J2CACHE(AbJ2CacheConfiguration.class);

    private Class<?> configurationClass;

    private AbCacheType(Class<?> configurationClass) {
        this.configurationClass = configurationClass;
    }

    public Class<?> getConfigurationClass() {
        return this.configurationClass;
    }
}
