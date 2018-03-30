package com.example.android.team49;

import java.util.ArrayList;

public class Recipe {

    String name = "";
    ArrayList<String> ingredients = new ArrayList<>();
    ArrayList<String> course = new ArrayList<>();
    String imageUrl = "";
    int rating = 0;
    String source;
    String id;
    ArrayList<String> cuisine = new ArrayList<>();

    public Recipe(String name, ArrayList<String> ingredients, ArrayList<String> course, ArrayList<String> cuisine,
                  String imageUrl, int rating, String source, String id) {
        this.name = name;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.source = source;
        this.id = id;
        this.course = course;
        this.cuisine = cuisine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<String> getCuisine() {
        return cuisine;
    }

    public void setCuisine(ArrayList<String> cuisine) {
        this.cuisine = cuisine;
    }

    @Override
    public String toString() {
        String toReturn =  "Name = " + name + "\n Ingredients = " + ingredients.toString() + "\n Course = " + course.toString()
                + "\n Cuisine = " + cuisine.toString() + "\n ImageURL = " +imageUrl + "\n Rating = " + rating +
                "\n Source = " + source + "\n ID = " +id;
        return toReturn;
    }

}


