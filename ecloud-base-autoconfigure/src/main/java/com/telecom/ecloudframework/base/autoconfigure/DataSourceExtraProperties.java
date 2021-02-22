package com.telecom.ecloudframework.base.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.datasource"
)
public class DataSourceExtraProperties {
    private String dbType;

    public DataSourceExtraProperties() {
    }

    public String getDbType() {
        return this.dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
