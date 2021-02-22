package com.telecom.ecloudframework.base.autoconfigure.cache;


import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;

class AbCacheConditional extends SpringBootCondition {
    AbCacheConditional() {
    }

    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata)metadata).getClassName();
        }

        String value = context.getEnvironment().getProperty("ecloud.cache.type");
        if (StringUtils.isEmpty(value)) {
            return AbMemoryCacheConfiguration.class.getName().equals(sourceClass) ? ConditionOutcome.match() : ConditionOutcome.noMatch(value + " cache type");
        } else {
            value = value.replace("-", "_").toUpperCase();
            AbCacheType abCacheType = AbCacheType.valueOf(value);
            return abCacheType.getConfigurationClass().getName().equals(sourceClass) ? ConditionOutcome.match() : ConditionOutcome.noMatch(value + " cache type");
        }
    }
}

