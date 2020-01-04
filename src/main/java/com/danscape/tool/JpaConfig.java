package com.danscape.tool;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories( //
        basePackages = "com.danscape.tool.jpa.repository", //
        entityManagerFactoryRef = "entityManagerFactory", //
        transactionManagerRef = "transactionManager"//
)
@Slf4j
public class JpaConfig {

    @Resource
    private JpaProperties jpaProperties;
    
    /**
     * Data source.
     *
     * @return the data source
     */
    @Primary
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource dataSource = null;
        for (int i = 0; i < jpaProperties.getInitialConnectionRetryCount(); i++) {
            if (dataSource != null) {
                continue;
            }
            try {
                final HikariConfig config = new HikariConfig();
                config.setDriverClassName(jpaProperties.getDriverClass());
                config.setJdbcUrl(jpaProperties.getUrl());
                config.setUsername(jpaProperties.getUsername());
                config.setPassword(jpaProperties.getPassword());
                config.setMinimumIdle(jpaProperties.getConnectionsMinIdle());
                config.setMaximumPoolSize(jpaProperties.getConnectionsMaxIdle());
//                config.addDataSourceProperty("rewriteBatchedStatements", "true");
                dataSource = new HikariDataSource(config);
            } catch (final Exception ex) {
                log.info(ex.getMessage());
                log.info(
                        "Initial connection may have failed due to DB load or network load. Sleep for a period of time and try to reconnect.");
                try {
                    Thread.sleep(jpaProperties.getInitialConnectionRetrySleep());
                } catch (final InterruptedException e) {
                    throw new RuntimeException(//
                            "Initial connection thread sleep error. application abort."//
                    );
                }
            }
        }

        return dataSource;
    }

    /**
     * Jpa vendor adapter.
     *
     * @return the jpa vendor adapter
     */
    private JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(Boolean.FALSE);
        jpaVendorAdapter.setShowSql(Boolean.TRUE);
        return jpaVendorAdapter;
    }

    /**
     * Entity manager factory.
     *
     * @return the entity manager factory
     */
    @Primary
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setPackagesToScan(new String[] { "com.danscape.tool.jpa.entity" });
        factory.setDataSource(dataSource());

        factory.afterPropertiesSet();
        return factory.getObject();
    }

    /**
     * Transaction manager.
     *
     * @return the platform transaction manager
     */
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        txManager.setDefaultTimeout(jpaProperties.getTransactionTimeout());
        return txManager;
    }

    /**
     * Jdbc template.
     *
     * @return the jdbc template
     */
    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

}

