package com.telecom.ecloudframework.base.autoconfigure.cache;


import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.cache.impl.RedisCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional({AbCacheConditional.class})
public class AbRedisCacheConfiguration {
    public AbRedisCacheConfiguration() {
    }

    @Bean
    public ICache iCache() {
        return new RedisCache();
    }
}