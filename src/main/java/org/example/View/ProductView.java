package org.example.View;

import org.example.Controller.ProductController;
import org.example.Model.ProductModel;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import java.util.*;

public class ProductView {
    private static final Scanner in = new Scanner(System.in);
    // Product Controller Instance
    private static final ProductController productController = new ProductController();
    // Color Reset
    private static final String ANSI_RESET = "\u001B[0m";
    // Color Red
    private static final String ANSI_RED = "\u001B[31m";
    // Color Green
    private static final String ANSI_GREEN = "\u001B[32m";
    // Align Text Center
    private static final CellStyle text_align = new CellStyle(CellStyle.HorizontalAlign.CENTER);
    // Table Method
    private static Table table(){
        Table table = new Table(5, BorderStyle.UNICODE_HEAVY_BOX, ShownBorders.ALL);
        table.setColumnWidth(0, 15, 15);  // ID column width
        table.setColumnWidth(1, 25, 25);  // Name column width
        table.setColumnWidth(2, 25, 25);  // Unit Price column width
        table.setColumnWidth(3, 25, 25);  // Stock QTY column width
        table.setColumnWidth(4, 25, 25);  // Imported Date column width

        return  table;
    }
    // Table Header
    private static void tableHeader(Table table){
        table.addCell("ID", text_align);
        table.addCell("Name", text_align);
        table.addCell("Unit Price ($)", text_align);
        table.addCell("Stock Quantity", text_align);
        table.addCell("Imported Date", text_align);
    }
    // Output Message With Color Method
    private static void printColoredMessage(String message, String color) {
        System.out.print(color + message + ANSI_RESET);
    }
    // Number of products per page;
    private static int pageSize =  productController.getSpecificRow();
    // Validate User Input
    private static String validateInput(String prompt, String errorMessage, String regex){
        String user_input;
        do {
            System.out.print(prompt);
            user_input = in.nextLine();
            // If invalid name
            if (!user_input.matches(regex)) {
                printColoredMessage(errorMessage,  ANSI_RED);
            }else{
                // If not return name
                return user_input;
            }
        } while (true);
    }
    // Validate Option Method
    private static String validateInputOption() {
        String user_input;
        do {
            System.out.print("\n-> Choose an option : ");
            user_input = in.nextLine();
            // If input is invalid
            if (!user_input.matches("[a-zA-z]+")) {
                printColoredMessage("Invalid option! Please choose a valid option\n", ANSI_RED);
                continue;
            }
            return user_input;
        } while (true);
    }
    // Display Option Method
    private static String displayOption(){
        System.out.println("\nF)  First\t\tP)  Previous\t\tN)  Next\t\tL)  Last\t\tG) Go to specific page");

        System.out.println("\n*) Display All Option\n");

        System.out.println("W)  Write\t\tR)  Read\t\tU)  Update\t\tD)  Delete\t\tS) Search\t\tSe) Set Row");
        System.out.println("Sa) Save\t\tUn) Unsaved\t\tBa) BackUp\t\tRe) Restore\t\tE) Exit");

        return validateInputOption();
    }
    // Pagination method
    public static void displayAllOptions() {
        // Calculate a specific total product, total page, and start from page 1
        int pageNumber = 1;
        do {
            int totalProducts = productController.getTotalNumberOfProducts();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            // Get all records from get product method and store it in a list
            List<ProductModel> productList = productController.getProductsByPage(pageNumber, pageSize);

            // If there are any data
            if (!productList.isEmpty()) {
                Table table = table();
                table.addCell("Display All Products (Page " + pageNumber + ")", text_align, 5);
                tableHeader(table);
                // Display all records
                for (ProductModel product : productList) {
                    table.addCell(String.valueOf(product.getId()), text_align);
                    table.addCell(product.getName(), text_align);
                    table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                    table.addCell(String.valueOf(product.getStockQty()), text_align);
                    table.addCell(String.valueOf(product.getImportedDate()), text_align);
                }
                table.addCell("Page : " + pageNumber + " of " + totalPages,text_align, 2);
                table.addCell("Total Record : " + totalProducts, text_align, 3);
                System.out.println(table.render());
            } else {
                printColoredMessage("\n\nNo products found.\n\n", ANSI_RED);
            }
            // Display options
            String option = displayOption();

            switch (option.toUpperCase()) {
                case "F" -> pageNumber = 1;
                case "P" -> {
                    if (pageNumber > 1) {
                        pageNumber--;
                    } else {
                        printColoredMessage("Already on the first page.\n\n", ANSI_RED);
                    }
                }
                case "N" -> {
                    if (pageNumber < totalPages) {
                        pageNumber++;
                    } else {
                        printColoredMessage("Already on the last page.\n\n", ANSI_RED);
                    }
                }
                case "L" -> pageNumber = totalPages;
                case "G" -> {
                    int nextPage = Integer.parseInt(validateInput("Enter page number : ", "Page allow only positive and integer number.\n\n", "\\d+"));
                    if (nextPage < 1){
                        printColoredMessage("Page must be greater than zero.\n\n", ANSI_RED);
                    } else if (nextPage == 1) {
                        pageNumber = 1;
                    } else if (nextPage > totalPages) {
                        printColoredMessage("Page number exceeds total pages. Please enter a valid page number.\n\n", ANSI_RED);
                    } else {
                        pageNumber = nextPage;
                    }
                }
                case "W" -> insertProduct();
                case "R" -> displayProductByID();
                case "U" -> updateProduct();
                case "D" -> deleteProduct();
                case "S" -> searchProductByName();
                case "SE" -> setRow();
                case "SA" -> saveUnsavedData();
                case "UN" -> displayAllUnsavedData();
                case "BA" -> backupDatabase();
                case "RE" -> restoreDatabase();
                case "E" -> System.exit(0);
                default -> printColoredMessage("Invalid option! Please choose a valid option.\n\n", ANSI_RED);
            }
        } while (true);


    }

    // Display Product By ID Method
    private static void displayProductByID() {
        do {
            int id = Integer.parseInt(validateInput("\n=> Enter an ID to Search or 0 to go back : ", "ID Allow Only Integer Number And Positive Number\n","\\d+"));
            if (id == 0){
                return;
            }
            ProductModel product = productController.getProductById(id);

            // Create and initialize the table
            Table table = table();
            table.addCell("Products Found", text_align, 5);
            tableHeader(table);
            if (product != null) {
                table.addCell(String.valueOf(product.getId()), text_align);
                table.addCell(product.getName(), text_align);
                table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                table.addCell(String.valueOf(product.getStockQty()), text_align);
                table.addCell(String.valueOf(product.getImportedDate()), text_align);

                // Render and print the table
                System.out.println(table.render());
            } else {
                printColoredMessage("No product found with the given ID : " + id + "\n\n" , ANSI_RED);
            }

            int press = Integer.parseInt(validateInput("Press 1 to search again and 0 to cancel : ", "Invalid input! Please input only 1 or 0\n", "[01]"));
            if (press == 0){
                return;
            }
        } while (true);
    }
    // Search Product By Name
    private static void searchProductByName() {
        do {
            // Ask user to search product
            String name = validateInput("\n=> Enter a name to search or Back to go back : ", "Name allow only text\n","[a-zA-Z ]+");
            // If user press b then go back
            if (name.equalsIgnoreCase("BACK")){
                return;
            }
            // Call the get product by name method and store all data in a list
            List<ProductModel> productList = productController.getProductsByName(name);

            if (!productList.isEmpty()){
                // Create and initialize the table
                Table table = table();
                table.addCell("All Products Found", text_align, 5);
                tableHeader(table);
                // Display all records
                for (ProductModel product : productList) {
                    table.addCell(String.valueOf(product.getId()), text_align);
                    table.addCell(String.valueOf(product.getName()), text_align);
                    table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                    table.addCell(String.valueOf(product.getStockQty()), text_align);
                    table.addCell(String.valueOf(product.getImportedDate()), text_align);
                }
                // Render and print the table
                System.out.println(table.render());
            }else {
                printColoredMessage("No products found with the given name : " + name + "\n\n", ANSI_RED);
            }
            // Ask user to search again or go back
            int press = Integer.parseInt(validateInput("Press 1 to search again and 0 to cancel : ", "Invalid input! Please input only 1 or 0\n\n", "[01]"));
            // If user press 0 then go back
            if (press == 0){
                return;
            }
        }while (true);
    }
    // Insert Product Method
    private static void insertProduct() {
        // Ask user to input specific detail
        String name = validateInput("\n=> Enter product name : ", "Invalid input! Name allow only text.\n", "[a-zA-Z ]+");
        double unitPrice;
        do {
            unitPrice = Double.parseDouble(validateInput("=> Enter unit price : ", "Invalid input! Price allow only number and positive number.\n\n", "\\d+(\\.\\d+)?"));
            if (unitPrice == 0){
                printColoredMessage("Price must be greater than zero\n\n", ANSI_RED);
            }
        }while (unitPrice == 0);

        int stockQty;
        do {
            stockQty = Integer.parseInt(validateInput("=> Enter stock quantity : ", "Invalid input! Quantity allow only integer number and positive number.\n\n", "\\d+"));
            if (stockQty == 0){
                printColoredMessage("Stock Quantity must be greater than zero\n\n", ANSI_RED);
            }
        }while (stockQty == 0);

        // Create a ProductModel object with the retrieved ID and user-provided data
        ProductModel product = new ProductModel(0, name, unitPrice, stockQty, null);

        // Add the product to unsaved insertions table
        productController.insertProduct(product);

        printColoredMessage("Product added successfully! (Unsaved)\n\n", ANSI_GREEN);
    }
    // Update Product Method
    private static void updateProduct() {
        // Get the ID of the product to update
        int id = Integer.parseInt(validateInput("\n=> Enter the ID of the product to update or 0 to go back : ", "Invalid input! ID should be an integer.\n", "\\d+"));

        if (id == 0){
            return;
        }

        // Retrieve the product information from the controller
        ProductModel existingProduct = productController.getProductById(id);

        // Check if the product exists
        if (existingProduct != null) {
            // Get the updated information from the user
            String name = validateInput("\n=> Enter updated product name: ", "Invalid input! Name allow only text.\n", "[a-zA-Z ]+");
            double unitPrice;
            do {
                unitPrice = Double.parseDouble(validateInput("=> Enter updated unit price: ", "Invalid input! Price allow only number and positive number.\n\n", "\\d+(\\.\\d+)?"));
                if (unitPrice == 0){
                    printColoredMessage("Price must be greater than zero\n\n", ANSI_RED);
                }
            }while (unitPrice == 0);

            int stockQty;
            do {
                stockQty = Integer.parseInt(validateInput("=> Enter updated stock quantity: ", "Invalid input! Quantity allow only integer number and positive number.\n\n", "\\d+"));
                if (stockQty == 0){
                    printColoredMessage("Stock Quantity must be greater than zero\n\n", ANSI_RED);
                }
            }while (stockQty == 0);

            // Update the existing product model with the new information
            existingProduct.setName(name);
            existingProduct.setUnitPrice(unitPrice);
            existingProduct.setStockQty(stockQty);

            // Update the product through the controller to unsaved update table
            productController.updateProduct(existingProduct);

            printColoredMessage("Product updated successfully! (Unsaved)\n\n", ANSI_GREEN);
        } else {
            // If the product with the given ID does not exist
            printColoredMessage("Product with ID " + id + " does not exist.\n\n", ANSI_RED);
        }
    }
    // Save Unsaved Data Method
    private static void saveUnsavedData() {
        List<ProductModel> unsavedInsertions = productController.getUnsavedInsertions();
        List<ProductModel> unsavedUpdates = productController.getUnsavedUpdates();

        // Combine unsaved insertions and updates
        List<ProductModel> unsavedData = new ArrayList<>(unsavedInsertions);
        unsavedData.addAll(unsavedUpdates);

        // Sort the unsaved data by ID
        unsavedData.sort(Comparator.comparingInt(ProductModel::getId));

        Table table = table();
        table.addCell("Unsaved Products List", text_align, 5);
        tableHeader(table);
        // If there are any unsaved data
        if (!unsavedData.isEmpty()) {
            // Display all unsaved data
            for (ProductModel product : unsavedData) {
                table.addCell(String.valueOf(product.getId()), text_align);
                table.addCell(product.getName(), text_align);
                table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                table.addCell(String.valueOf(product.getStockQty()), text_align);
                table.addCell(String.valueOf(product.getImportedDate()), text_align);
            }
        } else {
            table.addCell("No Unsaved Data Found", text_align, 5);
        }
        // Render and print the table
        System.out.println(table.render());
        // If there are no data exit
        if (unsavedData.isEmpty()){
            printColoredMessage("\nPress any key to continue...", ANSI_GREEN);
            in.nextLine();
            return;
        }
        // Ask user to save specific unsaved data
        String choice = validateInput("\nChoose an option (Ui for save insertions, Uu for save updates, B for go back): ","Invalid input! Please choose Ui or Uu to save unsaved data.\n", "[a-zA-Z]+");

        if (choice.equalsIgnoreCase("B")){
            return;
        }
        // Switch user option
        switch (choice.toUpperCase()) {
            case "UI" -> {
                // Display unsaved update data
                displayUnsavedInsertions();
                // If there are no unsaved insertions data exit
                if (unsavedInsertions.isEmpty()){
                    printColoredMessage("\nPress any key to continue...", ANSI_GREEN);
                    in.nextLine();
                    break;
                }
                // Ask user to confirm
                String confirm = validateInput("\nDo you want to save unsaved insertions? (Y/N) : ", "Invalid input! Please enter 'Y' or 'N'.\n", "[yYnN]");
                // If user press y then save it to table products
                if (confirm.equalsIgnoreCase("Y")) {
                    productController.saveUnsavedInsertion();
                    printColoredMessage("Unsaved insertions data saved successfully!\n\n", ANSI_GREEN);
                } else {
                    printColoredMessage("Operation cancelled.\n", ANSI_RED);
                }
            }
            case "UU" -> {
                // Display unsaved update data
                displayUnsavedUpdates();
                // If there are no unsaved update data exit
                if (unsavedUpdates.isEmpty()){
                    printColoredMessage("\nPress any key to continue...", ANSI_GREEN);
                    in.nextLine();
                    break;
                }
                // Ask user to confirm
                String confirm = validateInput("\nDo you want to save unsaved updates? (Y/N) : ", "Invalid input! Please enter 'Y' or 'N'.\n", "[yYnN]");
                // If user press y then save it to table products
                if (confirm.equalsIgnoreCase("Y")) {
                    productController.saveUnsavedUpdate();
                    printColoredMessage("Unsaved updates data saved successfully!\n\n", ANSI_GREEN);
                } else {
                    printColoredMessage("Operation cancelled.\n", ANSI_RED);
                }
            }
            default -> printColoredMessage("Invalid choice. Please enter either 'Ui' or 'Uu' or 'B'.\n", ANSI_RED);
        }
    }
    // Display a list of unsaved products option
    private static void displayUnsavedDataOption() {
        do {
            // Ask user to choose an option
            System.out.println("\nI - For Unsaved Insertions and U - For Unsaved Updates B - For Go Back");
            String choice = validateInput("-> Choose which unsaved data you want to view : ","Invalid input! Please choose I or U to view unsaved data.\n\n", "[iIuUbB]");
            // If user press B exit
            if (choice.equalsIgnoreCase("b")){
                return;
            }
            // Switch user option
            switch (choice.toUpperCase()) {
                case "I" -> displayUnsavedInsertions();
                case "U" -> displayUnsavedUpdates();
                default -> printColoredMessage("Invalid choice. Please enter either 'I' or 'U' or 'B'.\n\n", ANSI_RED);
            }
        }while (true);
    }

    private static void displayAllUnsavedData() {
        // Get unsaved data then store it in a list
        List<ProductModel> unsavedInsertions = productController.getUnsavedInsertions();
        List<ProductModel> unsavedUpdates = productController.getUnsavedUpdates();

        // Combine unsaved insertions and updates
        List<ProductModel> unsavedData = new ArrayList<>(unsavedInsertions);
        unsavedData.addAll(unsavedUpdates);

        // Sort the unsaved data by ID
        unsavedData.sort(Comparator.comparingInt(ProductModel::getId));
        // Initialize table
        Table table = table();
        table.addCell("Unsaved Products List", text_align, 5);
        tableHeader(table);
        // If there are any unsaved data
        if (!unsavedData.isEmpty()) {
            // Display all unsaved data
            for (ProductModel product : unsavedData) {
                table.addCell(String.valueOf(product.getId()), text_align);
                table.addCell(product.getName(), text_align);
                table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                table.addCell(String.valueOf(product.getStockQty()), text_align);
                table.addCell(String.valueOf(product.getImportedDate()), text_align);
            }
        } else {
            table.addCell("No Unsaved Data Found", text_align, 5);
        }
        // Render and print the table
        System.out.println(table.render());
        if (unsavedData.isEmpty()){
            printColoredMessage("\nPress any key to continue...", ANSI_GREEN);
            in.nextLine();
            return;
        }
        displayUnsavedDataOption();
    }

    // Display unsaved insertion data
    private static void displayUnsavedInsertions() {
        // Get unsaved data and store it in a list
        List<ProductModel> unsavedInsertions = productController.getUnsavedInsertions();
        // Sort data by ID
        unsavedInsertions.sort(Comparator.comparingInt(ProductModel::getId));
        // Initialize table
        Table table = table();
        table.addCell("Unsaved Insertion Data", text_align, 5);
        tableHeader(table);
        // If there are any unsaved insertion data
        if (!unsavedInsertions.isEmpty()) {
            // Display all unsaved insertion data
            for (ProductModel product : unsavedInsertions) {
                table.addCell(String.valueOf(product.getId()), text_align);
                table.addCell(product.getName(), text_align);
                table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                table.addCell(String.valueOf(product.getStockQty()), text_align);
                table.addCell(String.valueOf(product.getImportedDate()), text_align);
            }
        } else {
            table.addCell("No Unsaved Insertion Data Found", text_align, 5);
        }
        // Render table
        System.out.println(table.render());
    }
    // Display unsaved update data
    private static void displayUnsavedUpdates() {
        // Get unsaved data and store it in a list
        List<ProductModel> unsavedUpdates = productController.getUnsavedUpdates();
        // Sort data by ID
        unsavedUpdates.sort(Comparator.comparingInt(ProductModel::getId));
        // Initialize table
        Table table = table();
        table.addCell("Unsaved Insertion Data", text_align, 5);
        tableHeader(table);
        // If there are any unsaved insertion data
        if (!unsavedUpdates.isEmpty()) {
            // Display all unsaved insertion data
            for (ProductModel product : unsavedUpdates) {
                table.addCell(String.valueOf(product.getId()), text_align);
                table.addCell(product.getName(), text_align);
                table.addCell(String.valueOf(product.getUnitPrice()), text_align);
                table.addCell(String.valueOf(product.getStockQty()), text_align);
                table.addCell(String.valueOf(product.getImportedDate()), text_align);
            }
        } else {
            table.addCell("No Unsaved Update Data Found", text_align, 5);
        }
        // Render table
        System.out.println(table.render());
    }
    // Delete Product By ID
    private static void deleteProduct() {
       do {
           // Get the ID of the product to delete
           int id = Integer.parseInt(validateInput("\n=> Enter the ID of the product to delete or 0 to go back : ", "Invalid input! ID should be an integer.\n", "\\d+"));
           // If user 0 press 0 exit
           if (id == 0){
               return;
           }
           String confirmation = validateInput("Are you sure you want to delete this item? (Y/N) : ", "Invalid input! Allow only 'Y' or 'N'.\n\n", "[yYnN]");
           // If user press N or n exit
           if (!confirmation.equalsIgnoreCase("Y")) {
               printColoredMessage("Delete operation cancelled.\n\n", ANSI_GREEN);
               return;
           }
           // If user press Y Attempt to delete the product from the database
           boolean success = productController.deleteProduct(id);

           // Display the result
           if (success) {
               printColoredMessage("Product with ID " + id + " deleted successfully!\n\n", ANSI_GREEN);
           } else {
               printColoredMessage("Failed to delete product with ID " + id + ". No product found.\n\n", ANSI_RED);
           }
           // Ask user to delete again or exit
           int press = Integer.parseInt(validateInput("Press 1 to delete again and 0 to cancel : ", "Invalid input! Please input only 1 or 0\n\n", "[01]"));
           // If user press 0 exit
           if (press == 0){
               return;
           }
       }while (true);
    }
    // Set Row Method
    private static void setRow() {
        do {
            // Ask user to input a specific row
            int newRow = Integer.parseInt(validateInput("=> Enter the new row value : ", "Invalid input! Row allow only positive and integer number.\n\n", "\\d+"));
            // If row is smaller than 1 ask user to input again
            if (newRow < 1){
                printColoredMessage("Row must be greater than zero.\n\n", ANSI_RED);
                continue;
            }
            // If row is greater than 1 that update the row and update it to set_row table
            boolean success = productController.setRow(newRow);
            // If it is successfully update then display a message
            if (success) {
                printColoredMessage("Row value updated successfully!\n\n", ANSI_GREEN);

                // Recalculate pageSize using the updated newSetRowValue
                pageSize = newRow;
                return;
            } else {
                printColoredMessage("Failed to update row value.\n\n", ANSI_RED);
            }
        } while (true);
    }

    // Backup Database Method
    private static void backupDatabase() {
        try {
            String backupFilePath = "D:/backup.sql"; // Backup file on the D drive
            // Ask user to confirm
            String confirmation = validateInput("Do you want to backup the database? (Y/N) : ", "Invalid input! Allow only 'Y' or 'N'.\n\n", "[yYnN]");
            // If user press N or n then exit
            if (!confirmation.equalsIgnoreCase("Y")) {
                printColoredMessage("Backup operation cancelled.\n\n", ANSI_GREEN);
                return;
            }
            // If user press Y then backup database
            productController.backupDatabase(backupFilePath);
            printColoredMessage("Database backup successful!\n\n", ANSI_GREEN);
        } catch (Exception e) {
            printColoredMessage("Database backup failed: " + e.getMessage() + "\n\n", ANSI_RED);
        }
    }

    // Restore Database Method
    private static void restoreDatabase() {
        try {
            String backupFilePath = "D:/backup.sql"; // Get backup file from the D drive

            // Ask the user for confirmation
            String confirmation = validateInput("Do you want to restore the database? (Y/N) : ", "Invalid input! Allow only 'Y' or 'N'.\n\n", "[yYnN]");
            // If user press N or n then exit
            if (!confirmation.equalsIgnoreCase("Y")) {
                printColoredMessage("Restore operation cancelled.\n\n", ANSI_GREEN);
                return;
            }
            // If user press Y then restore database
            productController.restoreDatabase(backupFilePath);
            printColoredMessage("Database restore successful!\n\n", ANSI_GREEN);
        } catch (Exception e) {
            printColoredMessage("Database restore failed: " + e.getMessage() + "\n\n", ANSI_RED);
        }
    }
}
