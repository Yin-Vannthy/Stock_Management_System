package org.example;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DbFunction db = new DbFunction();
        Connection conn = db.connect_to_db("kshrd_db","postgres","admin");
//        db.createTable(conn,"employees");
//        db.insertRow(conn, "employees", "Vannthy", "Cambodia");
        db.update(conn, "employees", 1, "Jisoo", "Korea");
        db.readData(conn, "employees");
        System.out.println();
    }
}