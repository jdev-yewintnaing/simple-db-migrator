package com.example.migrator;

import java.sql.Connection;

public interface Migration {
    String getVersion();
    String getDescription();
    String getChecksum();
    void up(Connection connection) throws Exception;
    void down(Connection connection) throws Exception;
} 