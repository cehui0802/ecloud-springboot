package com.telecom.ecloudframework.base.autoconfigure.cache;


import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.spring.FstRedisSerializer;
import com.telecom.ecloudframework.component.j2cache.J2CacheChannelFactoryBean;
import com.telecom.ecloudframework.component.j2cache.J2CacheImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

@Conditional({AbCacheConditional.class})
public class AbJ2CacheConfiguration {
    public AbJ2CacheConfiguration() {
    }

    @Bean
    public J2CacheChannelFactoryBean cacheChannel(RedisConnectionFactory redisConnectionFactory, Environment environment) {
        J2CacheChannelFactoryBean j2CacheChannelFactoryBean = new J2CacheChannelFactoryBean();
        j2CacheChannelFactoryBean.setRedisTemplate(this.createRedisTemplate(redisConnectionFactory, environment));
        return j2CacheChannelFactoryBean;
    }

    private RedisTemplate createRedisTemplate(RedisConnectionFactory redisConnectionFactory, Environment environment) {
        String serialization = environment.getProperty("j2cache.serialization");
        RedisSerializer redisSerializer = null;
        if (!StringUtils.isEmpty(serialization) && !StringUtils.equals(serialization, "java")) {
            if (StringUtils.equals(serialization, "fst")) {
                redisSerializer = new FstRedisSerializer();
            }
        } else {
            redisSerializer = new JdkSerializationRedisSerializer();
        }

        Assert.notNull(redisSerializer, "j2cache only supports java serialization or fst serialization");
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer((RedisSerializer)redisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ICache iCache() {
        return new J2CacheImpl();
    }

    @Bean(
            name = {"abRedisMessageContainer"}
    )
    @ConditionalOnMissingBean(
            name = {"abRedisMessageContainer"}
    )
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}

