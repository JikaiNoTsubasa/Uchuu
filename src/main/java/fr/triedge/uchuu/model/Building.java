package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class Building {

    private int id;
    private String name;

    private ArrayList<BuildingRecipeItem> recipeItems = new ArrayList<>();

    public ArrayList<BuildingRecipeItem> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(ArrayList<BuildingRecipeItem> recipeItems) {
        this.recipeItems = recipeItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
