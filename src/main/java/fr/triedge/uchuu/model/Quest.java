package fr.triedge.uchuu.model;

import fr.triedge.uchuu.utils.Utils;

import java.util.ArrayList;

public class Quest {

    private int id, level, duration, xp;
    private String name, description;
    private boolean repeatable;
    private ArrayList<Drop> drops = new ArrayList<>();

    private Plan plan;

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getDurationDisplay(){
        return Utils.minsToHoursDisplay(getDuration());
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (!isRepeatable())
            return "[Unique] "+name;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Drop> getDrops() {
        return drops;
    }

    public void setDrops(ArrayList<Drop> drops) {
        this.drops = drops;
    }
}
