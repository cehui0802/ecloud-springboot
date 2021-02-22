package com.telecom.ecloudframework.base.autoconfigure;


import com.telecom.ecloudframework.base.core.spring.ECloudScheduledBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ECloudScheduledAutoConfiguration {
    public ECloudScheduledAutoConfiguration() {
    }

    @Bean
    @ConditionalOnProperty(
            value = {"ecloud.schedule.enable"},
            havingValue = "true"
    )
    public ECloudScheduledBeanPostProcessor eCloudScheduledBeanPostProcessor() {
        return new ECloudScheduledBeanPostProcessor(true);
    }
}

