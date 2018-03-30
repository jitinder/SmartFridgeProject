package com.example.android.team49;

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by venet on 27/03/2018.
 */

public class Recipe {
    String recipe;
    ArrayList<String> ingredients = new ArrayList<>();
    ArrayList<String> course = new ArrayList<>();
    double[] flavours = new double[6];
    String imageUrl;
    int rating;
    String source;
    String id;

    String cuisine;

    public Recipe(String recipe, ArrayList<String> ingredients, ArrayList<String> course, double[] flavours, String cuisine,
                  String imageUrl, int rating,String source, String id) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.source = source;
        this.id = id;
        this.course = course;
        this.flavours = flavours;
        this.cuisine = cuisine;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getCourse() {
        return course;
    }

    public void setCourse(ArrayList<String> course) {
        this.course = course;
    }

    public double[] getFlavours() {
        return flavours;
    }

    public void setFlavours(double[] flavours) {
        this.flavours = flavours;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

}


