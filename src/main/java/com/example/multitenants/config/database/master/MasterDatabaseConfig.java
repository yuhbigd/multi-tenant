package com.example.multitenants.config.database.master;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.multitenants.entity.master.TenantEntity;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.example.multitenants.repository.master",
        "com.example.multitenants.repository.common" }, transactionManagerRef = "masterTransactionManager", entityManagerFactoryRef = "masterEntityManager")
@RequiredArgsConstructor
public class MasterDatabaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MasterDatabaseConfig.class);

    @Bean("masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin1234");
        dataSource.setSchema("public");
        return dataSource;
    }

    @Bean("masterEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(masterDataSource());
        em.setPackagesToScan(TenantEntity.class.getPackage().getName(), "com.example.multitenants.entity.common");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaPropertyMap(Map.ofEntries(
                Map.entry(org.hibernate.cfg.Environment.SHOW_SQL, true),
                Map.entry(org.hibernate.cfg.Environment.FORMAT_SQL, true),
                Map.entry(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")));
        return em;
    }

    @Bean("masterTransactionManager")
    @Primary
    public PlatformTransactionManager masterTransactionManager(EntityManagerFactory tenantEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(tenantEntityManager);
        return transactionManager;
    }
}
