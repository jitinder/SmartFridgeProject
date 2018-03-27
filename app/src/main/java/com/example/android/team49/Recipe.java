package com.example.android.team49;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by venet on 27/03/2018.
 */

public class Recipe {
    String recipe;
    ArrayList<String> ingredients = new ArrayList<>();
    String imageUrl;
    String rating;
    String source;
    String id;

    public Recipe() {}

    public Recipe(String recipe, ArrayList<String> ingredients, String imageUrl, String rating, String source, String id) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.source = source;
        this.id = id;
    }

    public String getRecipeName() {
        return recipe;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getImageUrl() {
        return  imageUrl;
    }

    public String getRating() {
        return rating;
    }

    public String getSource() {
        return source;
    }

    public String getId() { return id; }
}
