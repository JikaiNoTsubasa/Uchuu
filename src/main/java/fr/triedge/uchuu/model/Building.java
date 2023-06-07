package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class Building {

    private int id;
    private String name;

    private ArrayList<BuildingLevel> levels = new ArrayList<>();

    public ArrayList<BuildingLevel> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<BuildingLevel> levels) {
        this.levels = levels;
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
