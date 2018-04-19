package com.example.android.team49;

import java.util.ArrayList;

/**
 * Class used to represent the details of a Recipe
 *
 * @author      Sidak Pasricha
 */

public class Recipe {

    String name;
    String source;
    String imageURL;
    String sourceURL;
    ArrayList<String> ingredients;

    public Recipe(String name, String source, String imageURL, String sourceURL, ArrayList<String> ingredients) {
        this.name = name;
        this.source = source;
        this.imageURL = imageURL;
        this.sourceURL = sourceURL;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Recipe: Name = " + name + " Source =  " + source + " imageURL = " + imageURL + " SourceURL = " + sourceURL + " Ingredients = " +ingredients.toString();
    }
}


