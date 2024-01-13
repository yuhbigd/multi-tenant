package com.example.multitenants.config.database.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.multitenants.entity.tenant.Product;
import com.example.multitenants.util.CurrentTenant;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.example.multitenants.repository.tenant",
        "com.example.multitenants.repository.common" }, entityManagerFactoryRef = "tenantEntityManagerFactory", transactionManagerRef = "tenantTransactionManager")
public class TenantDatabaseConfig {

    @Bean("tenantDataSource")
    public DataSource masterDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin1234");
        return dataSource;
    }

    @Bean
    public CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver() {
        return new TenantSchemaSelector();
    }

    @Bean
    MultiTenantConnectionProvider<String> multiTenantConnectionProvider(
            @Qualifier("tenantDataSource") DataSource dataSource) {
        return new TenantConnectionProvider(dataSource);
    }

    @Bean("tenantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("tenantDataSource") DataSource dataSource,
            MultiTenantConnectionProvider<String> multiTenantConnectionProviderImpl,
            CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolverImpl) {
        final var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(Product.class.getPackage().getName(),
                "com.example.multitenants.entity.common");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaPropertyMap(Map.ofEntries(
                Map.entry("hibernate.multiTenancy", "SCHEMA"),
                Map.entry("hibernate.multi_tenant_connection_provider", multiTenantConnectionProviderImpl),
                Map.entry("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolverImpl),
                Map.entry(org.hibernate.cfg.Environment.SHOW_SQL, true),
                Map.entry(org.hibernate.cfg.Environment.FORMAT_SQL, true),
                Map.entry(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")));
        return entityManagerFactoryBean;
    }

    @Bean("tenantTransactionManager")
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory tenantEntityManager) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(tenantEntityManager);
        return transactionManager;
    }

    private static class TenantSchemaSelector implements CurrentTenantIdentifierResolver<String> {
        private static final Logger LOGGER = LoggerFactory.getLogger(TenantSchemaSelector.class);
        private static final String DEFAULT_SCHEMA = "master";

        @Override
        public String resolveCurrentTenantIdentifier() {
            final var tenant = Optional.ofNullable(CurrentTenant.get())
                    .orElse(DEFAULT_SCHEMA);
            LOGGER.debug("Resolving tenant identifier (tenant={})", tenant);
            return tenant;
        }

        @Override
        public boolean validateExistingCurrentSessions() {
            return false;
        }

    }

    private static class TenantConnectionProvider implements MultiTenantConnectionProvider<String> {

        private static final Logger LOGGER = LoggerFactory.getLogger(TenantConnectionProvider.class);

        private final DataSource dataSource;

        public TenantConnectionProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getAnyConnection() throws SQLException {
            return dataSource.getConnection();
        }

        @Override
        public void releaseAnyConnection(Connection connection) throws SQLException {
            connection.close();
        }

        // important
        @Override
        public Connection getConnection(String tenantIdentifier) throws SQLException {
            LOGGER.debug("Getting connection for a tenant (tenantIdentifier={})", tenantIdentifier);
            final var connection = getAnyConnection();
            connection.setSchema(tenantIdentifier);
            return connection;
        }

        @Override
        public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
            LOGGER.debug("Releasing connection for a tenant (tenantIdentifier={})", tenantIdentifier);
            releaseAnyConnection(connection);
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }

        @Override
        public boolean isUnwrappableAs(Class<?> unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }
    }
}
