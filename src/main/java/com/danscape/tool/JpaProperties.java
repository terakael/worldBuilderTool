package com.danscape.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class JpaProperties {

    @Value("${batch.jpa.driver.class}")
    private String driverClass;

    @Value("${batch.jpa.url}")
    private String url;

    @Value("${batch.jpa.username}")
    private String username;

    @Value("${batch.jpa.password}")
    private String password;

    @Value("${batch.jpa.connections.idle.max}")
    private Integer connectionsMaxIdle;

    @Value("${batch.jpa.connections.idle.min}")
    private Integer connectionsMinIdle;

    @Value("${batch.jpa.transaction.timeout}")
    private Integer transactionTimeout;

    @Value("${batch.jpa.initial.connection.retry.count}")
    private Integer initialConnectionRetryCount;

    @Value("${batch.jpa.initial.connection.retry.sleep}")
    private Integer initialConnectionRetrySleep;

}
