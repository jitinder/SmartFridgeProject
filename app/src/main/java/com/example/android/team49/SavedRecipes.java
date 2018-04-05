package com.example.android.team49;

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

/**
 * Created by venet on 05/04/2018.
 */

public class SavedRecipes {
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId(){
        return mId;
    }
    public void setId(String id){
        mId = id;
    }

    @com.google.gson.annotations.SerializedName("instanceId")
    private String instanceId;
    public String getInstanceId(){
        return instanceId;
    }
    public void setInstanceId(String instanceId){
        this.instanceId = instanceId;
    }

    @com.google.gson.annotations.SerializedName("recipeLink")
    private String recipeLink;
    public String getRecipeLink(){
        return recipeLink;
    }
    public void setRecipeLink(String instanceId){
        this.recipeLink = recipeLink;
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

    public SavedRecipes(String instanceId, String recipeLink){
        this.instanceId = instanceId;
        this.recipeLink = recipeLink;
    }

}
