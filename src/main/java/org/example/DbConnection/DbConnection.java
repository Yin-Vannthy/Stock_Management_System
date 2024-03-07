package org.example.DbConnection;

import java.io.*;
import java.sql.*;

public class DbConnection {

    // Database connection configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/kshrd_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";
    // Extract the database name from the DB_URL
    private static final String databaseName = "kshrd_db";

    // Connection method
    public Connection connection() {
        Connection connection;
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (connection == null) {
                System.out.println("Connection failed");
            }

            // Check if the required tables exist, create them if not
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS products (\n" +
                        "  id SERIAL,\n" +
                        "  name VARCHAR(150) NOT NULL,\n" +
                        "  unit_price DECIMAL(10,2) NOT NULL,\n" +
                        "  stock_quantity INT NOT NULL,\n" +
                        "  imported_date DATE DEFAULT CURRENT_DATE\n" +
                        ");");

                statement.execute("CREATE TABLE IF NOT EXISTS unsaved_insertion (\n" +
                        "  id INT,\n" +
                        "  name VARCHAR(150) NOT NULL,\n" +
                        "  unit_price DECIMAL(10,2) NOT NULL,\n" +
                        "  stock_quantity INT NOT NULL,\n" +
                        "  imported_date DATE DEFAULT CURRENT_DATE\n" +
                        ");");

                statement.execute("CREATE TABLE IF NOT EXISTS unsaved_update (\n" +
                        "  id INT,\n" +
                        "  name VARCHAR(150) NOT NULL,\n" +
                        "  unit_price DECIMAL(10,2) NOT NULL,\n" +
                        "  stock_quantity INT NOT NULL,\n" +
                        "  imported_date DATE DEFAULT CURRENT_DATE\n" +
                        ");");

                statement.execute("CREATE TABLE IF NOT EXISTS set_row (\n" +
                        "  id INT,\n" +
                        "  row INT\n" +
                        ");");

                // Insert initial data into products table if it's empty
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM products");
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    statement.executeUpdate("INSERT INTO products (name, unit_price, stock_quantity) VALUES\n" +
                            "('coca', 1.99, 100),\n" +
                            "('sting', 1.5, 50),\n" +
                            "('sprite', 2, 80),\n" +
                            "('tiger beer', 3.99, 120),\n" +
                            "('heniken', 5.99, 60);");
                }

                // Insert values into set_row table
                statement.executeUpdate("INSERT INTO set_row VALUES (1, 3);");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    // Backup the database to an SQL file
    public void backupDatabase(String backupFilePath) {
        try {
            // Specify the full path to pg_dumpall executable
            String pgDumpallPath = "C:/Program Files/PostgreSQL/16/bin/pg_dumpall.exe";

            // Use ProcessBuilder to run pg_dumpall command with password option
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pgDumpallPath, "-U", DB_USER, "-f", backupFilePath);

            // Set the password as an environment variable
            processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Capture standard output and standard error separately
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Backup process returned non-zero exit code: " + exitCode);
            }

            System.out.println("\nDatabase backup successful. File: " + backupFilePath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to backup database: " + e.getMessage());
        }
    }

    // Restore the database from an SQL file
    public void restoreDatabase(String backupFilePath) {
        try {
            String psqlPath = "C:/Program Files/PostgreSQL/16/bin/psql.exe";

            // Use ProcessBuilder to run psql command with password option
            ProcessBuilder processBuilder = new ProcessBuilder(
                    psqlPath, "-U", DB_USER, "-d", "template1", "-c", "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '" + databaseName + "' AND pid <> pg_backend_pid()");

            // Set the password as an environment variable
            processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();

            processBuilder = new ProcessBuilder(
                    psqlPath, "-U", DB_USER, "-d", "template1", "-c", "DROP DATABASE IF EXISTS " + databaseName);

            // Set the password as an environment variable
            processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();

            processBuilder = new ProcessBuilder(
                    psqlPath, "-U", DB_USER, "-d", "template1", "-c", "CREATE DATABASE " + databaseName);

            // Set the password as an environment variable
            processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();

            processBuilder = new ProcessBuilder(
                    psqlPath, "-U", DB_USER, "-d", databaseName, "-f", backupFilePath);

            // Set the password as an environment variable
            processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            // Capture standard output and standard error separately
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Restore process returned non-zero exit code: " + exitCode);
            }

            System.out.println("\nDatabase restore successful from file: " + backupFilePath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to restore database: " + e.getMessage());
        }
    }
}
