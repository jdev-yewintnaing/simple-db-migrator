package com.example.migrator;


import java.sql.Connection;

public class SqlMigration implements Migration {

    private final String version;

    private final String checksum;

    private final String description;

    private final String sql;

    public SqlMigration(String version, String checksum, String description, String sql) {

        this.version = version;
        this.checksum = checksum;
        this.description = description;
        this.sql = sql;
    }

    @Override
    public String getVersion() {

        return version;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String getChecksum() {

        return checksum;
    }

    @Override
    public void up(Connection connection) throws Exception {

        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }

    }

    @Override
    public void down(Connection connection) throws Exception {
        //TODO not implemented yet
    }

}
