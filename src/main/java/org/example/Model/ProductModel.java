package org.example.Model;

import java.util.Date;

public class ProductModel {
    private int id;
    private String name;
    private double unitPrice;
    private int stockQty;
    private final Date importedDate;

    public ProductModel(int id, String name, double unitPrice, int stockQty, Date importedDate) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.stockQty = stockQty;
        this.importedDate = importedDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public Date getImportedDate() {
        return importedDate;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", stockQty=" + stockQty +
                ", importedDate=" + importedDate +
                '}';
    }
}
