package com.example.android.team49;

/**
 * Created by venet on 20/03/2018.
 */


import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;


public class PinAccess {


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

    @com.google.gson.annotations.SerializedName("pin")
    private Integer mPin;
    public Integer getPin(){
        return mPin;
    }
    public void setPin(Integer pin){
        mPin = pin;
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

    public PinAccess(String name, Integer pin){
        this.setName(name);
        this.setPin(pin);
    }

}