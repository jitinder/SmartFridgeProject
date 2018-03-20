package com.example.android.team49;

/**
 * Created by venet on 12/03/2018.
 */

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;


public class Details {


    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId(){
        return mId;
    }
    public void setId(String id){
        mId = id;
    }

    @com.google.gson.annotations.SerializedName("username")
    private String mUsername;
    public String getUsername(){
        return mUsername;
    }
    public void setUsername(String username){
        mUsername = username;
    }

    @com.google.gson.annotations.SerializedName("password")
    private String mPassword;
    public String getPassword(){
        return mPassword;
    }
    public void setPassword(String password){
        mPassword = password;
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

    public Details(String username, String password){
        this.setUsername(username);
        this.setPassword(password);
    }

}
