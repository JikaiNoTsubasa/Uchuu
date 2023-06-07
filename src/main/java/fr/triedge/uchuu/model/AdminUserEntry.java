package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class AdminUserEntry {

    private User user;
    private Quest currentQuest;
    private RunningQuest runningQuest;
    private int nextLevelXP;
    private float nextLevelPercent;
    private ArrayList<UserBuilding> buildings = new ArrayList<>();

    public ArrayList<UserBuilding> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<UserBuilding> buildings) {
        this.buildings = buildings;
    }

    public RunningQuest getRunningQuest() {
        return runningQuest;
    }

    public void setRunningQuest(RunningQuest runningQuest) {
        this.runningQuest = runningQuest;
    }

    public float getNextLevelPercent() {
        return nextLevelPercent;
    }

    public void setNextLevelPercent(float nextLevelPercent) {
        this.nextLevelPercent = nextLevelPercent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void setCurrentQuest(Quest currentQuest) {
        this.currentQuest = currentQuest;
    }

    public int getNextLevelXP() {
        return nextLevelXP;
    }

    public void setNextLevelXP(int nextLevelXP) {
        this.nextLevelXP = nextLevelXP;
    }
}
