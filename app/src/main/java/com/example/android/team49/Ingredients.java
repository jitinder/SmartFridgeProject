package com.example.android.team49;

/**
 * Created by venet on 12/03/2018.
 */

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;


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
    private String mName;
    public String getName(){
        return mName;
    }
    public void setName(String name){
        mName = name;
    }

    @com.google.gson.annotations.SerializedName("expiration_date")
    private String mExpr;
    public String getExpr(){
        return mExpr;
    }
    public void setExpr(String expr){
        mExpr = expr;
    }

    @com.google.gson.annotations.SerializedName("quantity")
    private Integer mQuantity;
    public Integer getQuantity() { return mQuantity; }
    public void setQuantity(Integer quantity)
    {mQuantity = quantity;}

    @com.google.gson.annotations.SerializedName("instanceId")
    private String mInstanceId;
    public String getInstanceId(){
        return mInstanceId;
    }
    public void setInstanceId(String instanceId){
        mInstanceId = instanceId;
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

    public Ingredients(String name, String expr, Integer quantity, String instanceId){
        this.setName(name);
        this.setExpr(expr);
        this.setQuantity(quantity);
        this.setInstanceId(instanceId);
    }

}
