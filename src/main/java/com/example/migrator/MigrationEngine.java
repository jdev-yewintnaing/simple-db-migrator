package com.example.migrator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrationEngine {

    private final Connection connection;

    public MigrationEngine(Connection connection) {

        this.connection = connection;
    }

    public void migrate(String migrationPath) throws Exception {
        // Implement migration logic here
        // This could involve checking the current version, applying migrations, etc.
        // For example:
        // 1. Check the current version in the database
        // 2. Load available migrations from a directory
        // 3. Apply each migration that is newer than the current version
        // 4. Update the current version in the database after each successful migration

        // Placeholder for migration logic

        var migrationRepository = new MigrationRepository(connection);
        migrationRepository.ensureMigrationHistory();
        List<Migration> migrations = loadMigrations(migrationPath);
        HashMap<String, String> appliedMigrations = migrationRepository.getAppliedMigrationList();

        for (Migration migration : migrations) {

            // Check if the migration has already been applied
            String appliedChecksum = appliedMigrations.get(migration.getVersion());
            if (appliedChecksum != null && !appliedChecksum.equals(migration.getChecksum())) {
                throw new IllegalStateException(
                    "Migration checksum mismatch for version " + migration.getVersion() +
                        ". Expected: " + migration.getChecksum() + ", but found: " + appliedChecksum);
            }

            try {
                if (appliedChecksum == null) {
                    connection.setAutoCommit(false);
                    System.out.println("Applying migration: " + migration.getDescription());

                    migration.up(connection);
                    migrationRepository.recordMigration(migration.getVersion(),
                                                        migration.getDescription(),
                                                        migration.getChecksum());
                    connection.commit();
                } else {
                    System.out.println("Skipping already applied migration: " + migration.getDescription());
                }
            } catch (SQLException e) {
                System.err.println("Error applying migration " + migration.getVersion() + ": " + e.getMessage());
                connection.rollback();
                throw e; // Rethrow the exception to indicate failure
            }
        }

    }

    private List<Migration> loadMigrations(String folder) throws IOException {
        // Implement logic to load migrations from a specified directory
        // This could involve reading SQL files, creating Migration objects, etc.
        // For example:
        // 1. Read all .sql files from a directory
        // 2. Create Migration objects for each file
        // 3. Return the list of Migration objects

        Pattern pattern = Pattern.compile("V(\\d+)__(.+)\\.sql");

        List<Migration> migrationList = new ArrayList<>();
        Path migrationPath = Path.of(folder);

        try (var fileList = Files.list(migrationPath)) {

            fileList
                .filter(Files::isRegularFile)
                .filter(file -> file.toString().endsWith(".sql"))
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    Matcher matcher = pattern.matcher(fileName);
                    if (matcher.matches()) {
                        String version = matcher.group(1);
                        String description = matcher.group(2).replace('_', ' '); // Replace underscores with spaces
                        try {
                            String sql = Files.readString(path);
                            Migration migration = new SqlMigration(version,
                                                                   ChecksumUtil.calculateChecksum(path),
                                                                   description,
                                                                   sql);
                            migrationList.add(migration);
                        } catch (IOException e) {
                            System.err.println("Error loading migration from file " + fileName + ": " + e.getMessage());
                        }
                    }
                });

        }

        // sort version no
        migrationList.sort(Comparator.comparing(Migration::getVersion));

        return migrationList;
    }

}
