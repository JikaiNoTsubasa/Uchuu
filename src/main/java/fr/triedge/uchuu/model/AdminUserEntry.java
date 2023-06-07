package fr.triedge.uchuu.model;

public class AdminUserEntry {

    private User user;
    private Quest currentQuest;
    private RunningQuest runningQuest;
    private int nextLevelXP;
    private float nextLevelPercent;

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
