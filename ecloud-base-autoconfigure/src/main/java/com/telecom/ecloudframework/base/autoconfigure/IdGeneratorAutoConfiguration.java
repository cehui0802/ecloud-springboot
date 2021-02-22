package com.telecom.ecloudframework.base.autoconfigure;


import com.telecom.ecloudframework.base.core.id.IdGenerator;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.id.snowflake.SnowflakeIdGenerator;
import com.telecom.ecloudframework.base.core.id.snowflake.SnowflakeIdMeta;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableConfigurationProperties({IdGeneratorProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class IdGeneratorAutoConfiguration {
    public IdGeneratorAutoConfiguration() {
    }

    @Bean
    public IdGenerator defaultIdGenerator(JdbcTemplate jdbcTemplate, IdGeneratorProperties idGeneratorProperties) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(new SnowflakeIdMeta(idGeneratorProperties.getMachine(), idGeneratorProperties.getMachineBits(), idGeneratorProperties.getSequenceBits(), idGeneratorProperties.getTimeSequence()));
        return idGenerator;
    }

    @Bean
    public IdUtil uniqueIdUtil(IdGenerator idGenerator) {
        IdUtil uniqueIdUtil = new IdUtil();
        uniqueIdUtil.setIdGenerator(idGenerator);
        return uniqueIdUtil;
    }
}

