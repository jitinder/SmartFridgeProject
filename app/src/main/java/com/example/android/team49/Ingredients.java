package com.example.android.team49;

/**
 * Created by venet on 12/03/2018.
 */

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;


public class Ingredients {

    public Ingredients(String instanceID, String name, int barcodeNumber, String expDate, int quantity) {
        this.instanceID = instanceID;
        this.name = name;
        this.barcodeNumber = barcodeNumber;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId(){
        return mId;
    }
    public void setId(String id){
        mId = id;
    }

    @com.google.gson.annotations.SerializedName("name")
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @com.google.gson.annotations.SerializedName("expiration_date")
    private String expDate;
    public String getExpDate() {
        return expDate;
    }
    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    @com.google.gson.annotations.SerializedName("quantity")
    private int quantity;
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @com.google.gson.annotations.SerializedName("instanceId")
    private String instanceID;
    public String getInstanceId(){
        return instanceID;
    }
    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    @com.google.gson.annotations.SerializedName("createdat")
    private DateTimeOffset mCreatedAt;
    public DateTimeOffset getCreatedAt(){
        return mCreatedAt;
    }
    protected void setCreatedAt(DateTimeOffset createdAt) { mCreatedAt = createdAt; }

    @com.google.gson.annotations.SerializedName("updatedat")
    private DateTimeOffset mUpdatedAt;
    public DateTimeOffset getUpdatedAt() { return mUpdatedAt; }
    protected void setUpdatedAt(DateTimeOffset updatedAt) { mUpdatedAt = updatedAt; }

    @com.google.gson.annotations.SerializedName("barcode")
    private int barcodeNumber = 0;
    public int getBarcodeNumber() {
        return barcodeNumber;
    }
    public void setBarcodeNumber(int barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }

    @Override
    public String toString() {
        return "Name: " +name+ " Barcode: " +barcodeNumber+ " Exp: " +expDate+ " Quantity: " +quantity+ " to InstanceID: " +instanceID;
    }

}
