package com.telecom.ecloudframework.base.autoconfigure.cache;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@EnableConfigurationProperties({AbCacheProperties.class})
@Import({AbCacheAutoConfiguration.AbCacheConfigurationSelector.class})
@Configuration
public class AbCacheAutoConfiguration {
    public AbCacheAutoConfiguration() {
    }

    public static class AbCacheConfigurationSelector implements ImportSelector {
        public AbCacheConfigurationSelector() {
        }

        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            AbCacheType[] types = AbCacheType.values();
            String[] imports = new String[types.length];

            for(int i = 0; i < types.length; ++i) {
                imports[i] = types[i].getConfigurationClass().getName();
            }

            return imports;
        }
    }
}
