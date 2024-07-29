package com.org.pointmax.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseManager {
    private static final DataBaseManager instance = new DataBaseManager();

    public static DataBaseManager getInstance() {
        return instance;
    }

    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    private DataBaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/pointmax/locations.sqlite");
            statement = connection.createStatement();
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database connection or creating tables", e);
        }
    }

    private void initializeDatabase() {
        try {
            statement.execute("CREATE TABLE IF NOT EXISTS home_locations (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    uuid TEXT NOT NULL,\n" +
                    "    home_name TEXT NOT NULL,\n" +
                    "    world_name TEXT NOT NULL,\n" +
                    "    x REAL NOT NULL,\n" +
                    "    y REAL NOT NULL,\n" +
                    "    z REAL NOT NULL,\n" +
                    "    yaw REAL NOT NULL,\n" +
                    "    pitch REAL NOT NULL\n" +
                    ");");

            statement.execute("CREATE TABLE IF NOT EXISTS spawn_location (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    world_name TEXT NOT NULL,\n" +
                    "    x REAL NOT NULL,\n" +
                    "    y REAL NOT NULL,\n" +
                    "    z REAL NOT NULL,\n" +
                    "    yaw REAL NOT NULL,\n" +
                    "    pitch REAL NOT NULL\n" +
                    ");");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
