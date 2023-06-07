package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class Recipe {

    private int id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private ArrayList<RecipeItem> recipeItems = new ArrayList<>();

    public ArrayList<RecipeItem> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(ArrayList<RecipeItem> recipeItems) {
        this.recipeItems = recipeItems;
    }
}
