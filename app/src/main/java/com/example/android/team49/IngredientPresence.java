package com.example.android.team49;

/**
 * Class used to represent the presence of an Ingredient
 *
 * @author Sidak Pasricha
 */

public class IngredientPresence {
    private String name;
    private boolean present;

    public IngredientPresence(String name, boolean present) {
        this.name = name;
        this.present = present;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
