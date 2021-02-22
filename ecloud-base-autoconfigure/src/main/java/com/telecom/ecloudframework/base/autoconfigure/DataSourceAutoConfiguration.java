package com.telecom.ecloudframework.base.autoconfigure;


import com.telecom.ecloudframework.base.dao.baseinterceptor.QueryInterceptor;
import com.telecom.ecloudframework.base.dao.baseinterceptor.SaveInterceptor;
import com.telecom.ecloudframework.base.db.datasource.DynamicDataSource;
import com.github.pagehelper.PageInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

@EnableConfigurationProperties({DataSourceExtraProperties.class})
@Configuration
public class DataSourceAutoConfiguration {
    public DataSourceAutoConfiguration() {
    }

    @Primary
    @ConditionalOnClass({DataSource.class})
    @Bean
    public DataSource dataSourceDefault(DataSourceProperties dataSourceProperties) {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(dataSourceProperties.getDriverClassName());
        poolProperties.setUrl(dataSourceProperties.getUrl());
        poolProperties.setUsername(dataSourceProperties.getUsername());
        poolProperties.setPassword(dataSourceProperties.getPassword());
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setValidationQuery("SELECT COUNT(*) FROM ACT_GE_PROPERTY");
        poolProperties.setValidationQueryTimeout(300000);
        poolProperties.setTestWhileIdle(true);
        poolProperties.setTimeBetweenEvictionRunsMillis(60000);
        poolProperties.setMinEvictableIdleTimeMillis(60000);
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
        return dataSource;
    }

    @Bean
    public DynamicDataSource dataSource(DataSourceExtraProperties dataSourceExtraProperties, DataSource dataSourceDefault) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap(1);
        targetDataSources.put("dataSourceDefault", dataSourceDefault);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultDbtype(dataSourceExtraProperties.getDbType());
        return dynamicDataSource;
    }

    @Bean(
            name = {"jdbcTemplate"}
    )
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("autoRuntimeDialect", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    @Bean
    public QueryInterceptor queryInterceptor() {
        return new QueryInterceptor();
    }

    @Bean
    public SaveInterceptor saveInterceptor() {
        return new SaveInterceptor();
    }

    @Bean(
            name = {"abSqlSessionFactory"}
    )
    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, SaveInterceptor saveInterceptor) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(this.resolveMapperLocations("classpath*:cn/gwssi/ecloud/*/mapping/*.xml", "classpath*:cn/gwssi/ecloud/*/*/mapping/*.xml", "classpath*:cn/gwssi/ecloudframework/*/mapping/*.xml", "classpath*:cn/gwssi/ecloudframework/*/*/mapping/*.xml", "classpath*:cn/gwssi/ecloudbpm/*/mapping/*.xml", "classpath*:cn/gwssi/ecloudbpm/*/*/mapping/*.xml", "classpath*:com/samr/**/*.xml"));
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{this.pageInterceptor(), this.queryInterceptor(), saveInterceptor});
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties mysqlp = new Properties();
        mysqlp.setProperty("MySQL", "mysql");
        mysqlp.setProperty("Oracle", "oracle");
        mysqlp.setProperty("SQL Server", "mysql");
        mysqlp.setProperty("PostgreSQL", "mysql");
        mysqlp.setProperty("dmsql", "dmsql");
        mysqlp.setProperty("drds", "drds");
        mysqlp.setProperty("KingbaseES", "kingbase");
        databaseIdProvider.setProperties(mysqlp);
        sqlSessionFactoryBean.setDatabaseIdProvider(databaseIdProvider);
        return sqlSessionFactoryBean;
    }

    private Resource[] resolveMapperLocations(String... locations) {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList();
        String[] var4 = locations;
        int var5 = locations.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String mapperLocation = var4[var6];

            try {
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (IOException var9) {
            }
        }

        return (Resource[]) resources.toArray(new Resource[resources.size()]);
    }

    @Bean({"abMapperScannerConfigurer"})
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("abSqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.telecom.**.dao,com.samr.**.dao");
        return mapperScannerConfigurer;
    }

    @Bean({"abSqlSessionTemplate"})
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("abSqlSessionFactory") SqlSessionFactoryBean sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory.getObject());
    }
}
