package fr.triedge.uchuu.model;

public class UserBuilding {

    private int level;
    private boolean started;
    private Building building;

    public BuildingLevel getNextLevel(){
        if (getBuilding() == null || getBuilding().getLevels()== null || getBuilding().getLevels().size() ==0)
            return null;
        return getBuilding().getLevels().stream().filter(l -> l.getLevel()==getLevel()+1).findFirst().orElse(null);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}
