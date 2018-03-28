package com.example.android.team49;

/**
 * Created by Sidak Pasricha on 28-Mar-18.
 */

public class Ingredient {

    private String instanceID;
    private String name;
    private int barcodeNumber = 0;
    private String expDate;
    private int quantity;

    public Ingredient(String instanceID, String name, int barcodeNumber, String expDate, int quantity) {
        this.instanceID = instanceID;
        this.name = name;
        this.barcodeNumber = barcodeNumber;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    public Ingredient(String instanceID, String name, String expDate, int quantity) {
        this.instanceID = instanceID;
        this.name = name;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(int barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
