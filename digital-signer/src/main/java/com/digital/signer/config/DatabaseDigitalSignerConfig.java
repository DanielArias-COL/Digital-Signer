package com.digital.signer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseDigitalSignerConfig {

    @Autowired
    private Environment env;

    @Bean(name = "digitalSignerDataSource")
    public DataSource digitalSignerDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("database.datasource.url"));
        dataSource.setUsername(env.getProperty("database.datasource.username"));
        dataSource.setPassword(env.getProperty("database.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("database.datasource.driver-class-name"));
        return dataSource;
    }
}