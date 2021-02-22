package com.telecom.ecloudframework.base.autoconfigure.cache;


import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.cache.impl.MemoryCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional({AbCacheConditional.class})
public class AbMemoryCacheConfiguration {
    public AbMemoryCacheConfiguration() {
    }

    @Bean
    public ICache iCache() {
        return new MemoryCache();
    }
}

