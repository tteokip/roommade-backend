package com.roommade.global.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

public class FlywayMigrator {

    private DataSource dataSource;
    private String locations;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .load()
                .migrate();
    }
}
