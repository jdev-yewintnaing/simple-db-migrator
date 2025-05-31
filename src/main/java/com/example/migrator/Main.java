package com.example.migrator;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Usage: java -jar migrator.jar <jdbcUrl> <username> <password>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String username = args[1];
        String password = args[2];


        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            MigrationEngine runner = new MigrationEngine(connection);
            runner.migrate("src/main/resources/migrations");
            System.out.println("Migrations completed successfully");
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 