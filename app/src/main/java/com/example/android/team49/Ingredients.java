package com.example.android.team49;

/**
 * Class for Ingredients stored in the Azure Easy Table
 *
 * @authors      Sidak Pasricha, Venet Kukran
 */

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

import java.util.Comparator;


public class Ingredients {

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
    private int barcodeNumber;
    public int getBarcodeNumber() {
        return barcodeNumber;
    }
    public void setBarcodeNumber(int barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }


    public Ingredients(String instanceID, String name, int barcodeNumber, String expDate, int quantity) {
        this.instanceID = instanceID;
        this.name = name;
        this.barcodeNumber = barcodeNumber;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    /**
     * Compares 2 Ingredients' names and returns them in Ascending order
     */
    public static Comparator<Ingredients> compareNameAscending = new Comparator<Ingredients>() {
        @Override
        public int compare(Ingredients o1, Ingredients o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();

            return name1.compareTo(name2);
        }
    };

    /**
     * Compares 2 Ingredients' names and returns them in Descending order
     */
    public static Comparator<Ingredients> compareNameDescending = new Comparator<Ingredients>() {
        @Override
        public int compare(Ingredients o1, Ingredients o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();

            return name2.compareTo(name1);
        }
    };

    /**
     * Compares 2 Ingredients' quantities and returns them in Ascending order
     */
    public static Comparator<Ingredients> compareQuantityAscending = new Comparator<Ingredients>() {
        @Override
        public int compare(Ingredients o1, Ingredients o2) {
            int quantity1 = o1.getQuantity();
            int quantity2 = o2.getQuantity();

            return quantity1-quantity2;
        }
    };

    /**
     * Compares 2 Ingredients' quantities and returns them in Descending order
     */
    public static Comparator<Ingredients> compareQuantityDescending = new Comparator<Ingredients>() {
        @Override
        public int compare(Ingredients o1, Ingredients o2) {
            int quantity1 = o1.getQuantity();
            int quantity2 = o2.getQuantity();

            return quantity2-quantity1;
        }
    };

    /**
     * Return a String representation of the Object Ingredients
     *
     * @return      The String representation of this object
     */
    @Override
    public String toString() {
        return "Name: " +name+ " Barcode: " +barcodeNumber+ " Exp: " +expDate+ " Quantity: " +quantity+ " to InstanceID: " +instanceID;
    }

}
