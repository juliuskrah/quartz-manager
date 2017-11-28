package com.juliuskrah.quartz;

import javax.sql.DataSource;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("heroku")
public class RelationalCloudDatasourceConfig extends AbstractCloudConfig {

    @Bean
    DataSource dataSource () {
        return connectionFactory().dataSource();
    }
}