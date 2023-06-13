package fr.triedge.uchuu.model;

public class UserInQuest {
    private long startTime, endTime;

    private User user;
    private Quest quest;

    public boolean isFinished(){
        long cur = new java.util.Date().getTime();
        if (cur>=endTime){
            return true;
        }
        return false;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }
}
