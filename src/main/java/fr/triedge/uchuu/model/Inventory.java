package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class Inventory {

    private int id;
    private ArrayList<InventoryRow> items = new ArrayList<>();

    public ArrayList<InventoryRow> getItems() {
        return items;
    }

    public void setItems(ArrayList<InventoryRow> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
