package com.example.migrator;

import java.sql.Connection;
import java.util.HashMap;

public class MigrationRepository {

    private final Connection connection;

    public MigrationRepository(Connection connection) {

        this.connection = connection;
    }

    public void ensureMigrationHistory() throws Exception {

        String sql = "CREATE TABLE IF NOT EXISTS migrations (" +
            "version VARCHAR(255) PRIMARY KEY, " +
            "description TEXT, " +
            "checksum VARCHAR(64), " +
            "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void recordMigration(String version, String description, String checksum) throws Exception {

        String sql = "INSERT INTO migrations (version, description, checksum) VALUES (?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, version);
            pstmt.setString(2, description);
            pstmt.setString(3, checksum);
            pstmt.executeUpdate();
        }
    }

    public boolean migrationExists(String version) throws Exception {

        String sql = "SELECT COUNT(*) FROM migrations WHERE version = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, version);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteMigration(String version) throws Exception {

        String sql = "DELETE FROM migrations WHERE version = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, version);
            pstmt.executeUpdate();
        }
    }

    public HashMap<String, String> getAppliedMigrationList() throws Exception {

        String sql = "SELECT version, checksum FROM migrations ORDER BY version, applied_at";
        HashMap<String, String> migrationList = new HashMap<>();
        try (var pstmt = connection.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String version = rs.getString("version");
                String checksum = rs.getString("checksum");
                migrationList.put(version, checksum);
            }
        }
        return migrationList;

    }

}
