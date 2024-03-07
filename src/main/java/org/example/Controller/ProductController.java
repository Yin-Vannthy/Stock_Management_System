package org.example.Controller;

import org.example.DbConnection.DbConnection;
import org.example.Model.ProductModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductController {
    private DbConnection dbConnection;
    public ProductController() {
        dbConnection = new DbConnection();
    }

    // Get all product method
    public List<ProductModel> getProductsByPage(int pageNumber, int pageSize) {
        List<ProductModel> productList = new ArrayList<>();
        // Get a specific record number to offset (Skip)
        int offset = (pageNumber - 1) * pageSize;

        try (Connection conn = dbConnection.connection()) {
            // Query used to get all record from products table then order it by id and offset it by specific number
            String sql = "SELECT * FROM products Order By id ASC LIMIT ? OFFSET ? ";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, pageSize);
                statement.setInt(2, offset);

                try (ResultSet resultSet = statement.executeQuery()) {
                    // If there are any data store it in a variable
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        double unitPrice = resultSet.getDouble("unit_price");
                        int stockQty = resultSet.getInt("stock_quantity");
                        Date importedDate = resultSet.getDate("imported_date");
                        // Add it to array list
                        ProductModel product = new ProductModel(id, name, unitPrice, stockQty, importedDate);
                        productList.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }
    // Get product ID specific ID method
    public ProductModel getProductById(int id) {
        ProductModel product = null;

        try (Connection connection = dbConnection.connection()){
            // Query used to get a record by specific ID
            String query = "SELECT * FROM products WHERE id = ?";

            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, id);
                try(ResultSet resultSet = statement.executeQuery()){
                    // If there are any data store it in a variable
                    if (resultSet.next()){
                        String name = resultSet.getString("name");
                        double unitPrice = resultSet.getDouble("unit_price");
                        int stockQty = resultSet.getInt("stock_quantity");
                        Date importedDate = resultSet.getDate("imported_date");
                        // Add it to object product
                        product = new ProductModel(id, name, unitPrice, stockQty, importedDate);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return product;
    }
    // Get products by searching name method
    public List<ProductModel> getProductsByName(String name) {
        List<ProductModel> productList = new ArrayList<>();

        try(Connection connection = dbConnection.connection()){
            // Query used to get all record from products table and order it by ID
            String query = "SELECT * FROM products WHERE name ILIKE ? ORDER BY id";

            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, "%" + name + "%");
                // Store it in ResultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Check if there are any data store it in a variable
                    while (resultSet.next()){
                        int id = resultSet.getInt("id");
                        String productName = resultSet.getString("name");
                        double unitPrice = resultSet.getDouble("unit_price");
                        int stockQty = resultSet.getInt("stock_quantity");
                        Date importedDate = resultSet.getDate("imported_date");
                        // Add it to array list that it will be used to display
                        ProductModel product = new ProductModel(id, productName, unitPrice, stockQty, importedDate);
                        productList.add(product);
                    }
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return productList;
    }
    // Method to get the next available ID from the data source
    public int getNextAvailableId() {
        int nextId = 0;
        try (Connection connection = dbConnection.connection()) {
            // Query used to get the last ID from actual table that it will be use in insert data to a temporary table
            String query = "SELECT MAX(id) FROM products";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Plus the last ID with 1
                        nextId = resultSet.getInt(1) + 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextId;
    }
    // Insert Product method and insert it to temporary table
    public void insertProduct(ProductModel product) {
        try (Connection connection = dbConnection.connection()) {
            // // Query used to insert data after update it to a temporary table
            String query = "INSERT INTO unsaved_insertion(id, name, unit_price, stock_quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, product.getId()); // Assuming the ID is set in the product model
                statement.setString(2, product.getName());
                statement.setDouble(3, product.getUnitPrice());
                statement.setInt(4, product.getStockQty());
                // Execute query
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Update Product method and insert it to temporary table
    public void updateProduct(ProductModel product) {
        try (Connection connection = dbConnection.connection()) {
            // Query used to insert data after update it to a temporary table
            String query = "INSERT INTO unsaved_update(id, name, unit_price, stock_quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, product.getId());
                statement.setString(2, product.getName());
                statement.setDouble(3, product.getUnitPrice());
                statement.setInt(4, product.getStockQty());
                // Execute query
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Delete product by ID method
    public boolean deleteProduct(int id) {
        try (Connection conn = dbConnection.connection()) {
            // Query used to delete a record by specific ID
            String sql = "DELETE FROM products WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, id);
                // Execute Query
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Get unsaved insertions method used to display
    public List<ProductModel> getUnsavedInsertions() {
        List<ProductModel> unsavedInsertions = new ArrayList<>();

        try (Connection connection = dbConnection.connection()) {
            // Query used to get all record from temporary table
            String query = "SELECT * FROM unsaved_insertion ORDER BY  id";

            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                // Check if there are data store it in a variable
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double unitPrice = resultSet.getDouble("unit_price");
                    int stockQty = resultSet.getInt("stock_quantity");
                    Date importedDate = resultSet.getDate("imported_date");
                    // Add it to array list
                    ProductModel product = new ProductModel(id, name, unitPrice, stockQty, importedDate);
                    unsavedInsertions.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unsavedInsertions;
    }
    // Get unsaved update data method used to display
    public List<ProductModel> getUnsavedUpdates() {
        List<ProductModel> unsavedUpdates = new ArrayList<>();

        try (Connection connection = dbConnection.connection()) {
            // Query used to get all record from temporary table
            String query = "SELECT * FROM unsaved_update ORDER BY id";

            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                // Check if there are data store it in a variable
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double unitPrice = resultSet.getDouble("unit_price");
                    int stockQty = resultSet.getInt("stock_quantity");
                    Date importedDate = resultSet.getDate("imported_date");
                    // Add it to array list
                    ProductModel product = new ProductModel(id, name, unitPrice, stockQty, importedDate);
                    unsavedUpdates.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unsavedUpdates;
    }
    // Save unsaved insertions and updates to products table method
    public void saveUnsavedInsertion() {
        try (Connection connection = dbConnection.connection()) {
            // Query used to get all record from temporary table
            String selectQuery = "SELECT * FROM unsaved_insertion";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStatement.executeQuery()) {
                // Check if there are data store it in a variable
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    double unitPrice = resultSet.getDouble("unit_price");
                    int stockQty = resultSet.getInt("stock_quantity");
                    // If there are any data insert it to actual table
                    String insertQuery = "INSERT INTO products(name, unit_price, stock_quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertStatement.setString(1, name);
                        insertStatement.setDouble(2, unitPrice);
                        insertStatement.setInt(3, stockQty);
                        // Execute Query
                        int rowsAffected = insertStatement.executeUpdate();
                        // If succeed output a message
                        if (rowsAffected > 0) {
                            ResultSet rs = insertStatement.getGeneratedKeys();
                            if (rs.next()) {
                                String pName = rs.getString(2);
                                System.out.println("* New product : " + pName + " was saved to database *");
                            }
                        }
                    }
                }
            }

            // Clear unsaved_insertion table after saving
            String deleteQuery = "DELETE FROM unsaved_insertion";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Save unsaved update and updates to database method
    public void saveUnsavedUpdate() {
        try (Connection connection = dbConnection.connection()) {
            // Query used to get all record from temporary table
            String selectQuery = "SELECT * FROM unsaved_update";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStatement.executeQuery()) {
                // Check if there are data store it in a variable
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double unitPrice = resultSet.getDouble("unit_price");
                    int stockQty = resultSet.getInt("stock_quantity");
                    // If there are any data insert it to actual table
                    String updateQuery = "UPDATE products SET name = ?, unit_price = ?, stock_quantity = ? WHERE id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, name);
                        updateStatement.setDouble(2, unitPrice);
                        updateStatement.setInt(3, stockQty);
                        updateStatement.setInt(4, id);
                        // Execute Query
                        int rowsAffected = updateStatement.executeUpdate();
                        // If succeed output a message
                        if (rowsAffected > 0) {
                            System.out.println("* Updated product with ID: " + id + "was saved to database *");
                        }
                    }
                }
            }

            // Clear unsaved_update table after saving
            String deleteQuery = "DELETE FROM unsaved_update";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Count all record in products method
    public int getTotalNumberOfProducts() {
        int totalNumberOfProducts = 0;

        try (Connection conn = dbConnection.connection()) {
            // Query count all record in products table
            String sql = "SELECT COUNT(*) FROM products";
            try (Statement statement = conn.createStatement();
                 // Store it in ResultSet
                 ResultSet resultSet = statement.executeQuery(sql)) {
                // If there are any data store it in a variable
                if (resultSet.next()) {
                    totalNumberOfProducts = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalNumberOfProducts;
    }
    // Get specific row used to display each record for each page method
    public int getSpecificRow() {
        int specificRow = 0;

        try (Connection connection = dbConnection.connection()) {
            // Get value from specific column
            String query = "SELECT row FROM set_row";
            // prepare statement
            try (PreparedStatement statement = connection.prepareStatement(query);
                 // Store data in ResultSet
                 ResultSet resultSet = statement.executeQuery()) {
                // Get value
                if (resultSet.next()) {
                    specificRow = resultSet.getInt("row");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return specificRow;
    }
    // When user set row update it to set_row table
    public boolean setRow(int newRow) {
        try (Connection connection = dbConnection.connection()) {
            // Update query
            String query = "UPDATE set_row SET row = ?";
            // Prepare statement before update
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, newRow);
                // Update data
                int rowsAffected = preparedStatement.executeUpdate();
                // Returns true if rows were affected (update successful)
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Return false to indicate an error
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Method to back_up the database
    public void backupDatabase(String backupFilePath) {
        dbConnection.backupDatabase(backupFilePath);
    }
    // Method to restore the database
    public void restoreDatabase(String sqlFilePath) {
        dbConnection.restoreDatabase(sqlFilePath);
    }
}
