package org.example;

import java.sql.*;

public class DbFunction {
    public Connection connect_to_db(String dbName, String user, String password) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbName, user, password);

            if (conn != null) {
                System.out.println("Connection Established");
            } else {
                System.out.println("Connection Failed");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createTable(Connection conn, String tableName) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "CREATE TABLE " + tableName + " (empId SERIAL PRIMARY KEY, name varchar(150), address varchar(150))";
            statement.executeUpdate(query);
            System.out.println("Table Created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void insertRow(Connection conn, String tableName, String name, String address) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String query = "INSERT INTO " + tableName + "(name, address) VALUES('" + name + "', '" + address + "')";
            statement.executeUpdate(query);
            System.out.println("Inserted");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void readData(Connection conn, String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            String query = "SELECT * FROM " + tableName;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("empId");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                System.out.println("id : " + id);
                System.out.println("name : " + name);
                System.out.println("address : " + address);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update(Connection conn, String tableName, int id, String newName, String address){
        Statement statement = null;
        try{
            String query = "UPDATE " + tableName + " SET name = '" + newName + "', address = '" + address + "' WHERE empid = " + id + ";";
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Updated");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
