package com.example.multitenants.config;

import java.util.Set;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final var context = event.getApplicationContext();
        final var environment = context.getEnvironment();
        var tenants = Set.of("tenant1", "tenant2");
        for (final var schema : tenants) {
            LOGGER.info("Migrating tenant schema (schema={})", schema);

            final var flyway = Flyway.configure()
                    .dataSource(context.getBean(DataSource.class))
                    .locations(environment.getRequiredProperty("application.db.tenant.locations"))
                    .schemas(schema)
                    .load();

            final var migrationResult = flyway.migrate();

            LOGGER.info(
                    "Tenant schema migrated successfully (schema={}, success={})",
                    migrationResult.schemaName,
                    migrationResult.success);
        }
        var masterSchema = "public";

        final var flyway = Flyway.configure()
                .dataSource(context.getBean(DataSource.class))
                .locations(environment.getRequiredProperty("application.db.master.locations"))
                .baselineOnMigrate(true)
                .schemas(masterSchema)
                .load();

        final var migrationResult = flyway.migrate();

        LOGGER.info(
                "Master schema migrated successfully (schema={}, success={})",
                migrationResult.schemaName,
                migrationResult.success);
    }

}
