package fr.triedge.uchuu.model;


import java.util.HashMap;

public class Model {
    //region VARIABLES #######################################/
    private HashMap<Integer, User> users = new HashMap<>();
    private HashMap<Integer, Quest> quests = new HashMap<>();
    private HashMap<User, UserInQuest> userInQuest = new HashMap<>();
    //endregion

    //region FUNCTIONS ######################################/
    public User getUser(int id){
        return getUsers().get(id);
    }

    public User getUser(User user){
        if (user == null)return null;
        return getUser(user.getId());
    }
    public Quest getQuest(int id){
        return getQuests().get(id);
    }

    public Quest getQuest(Quest quest){
        if (quest == null)return null;
        return getQuest(quest.getId());
    }

    public UserInQuest getRunningQuest(int userId){
        User u = getUser(userId);
        if (u == null)return null;
        return getUserInQuest().get(u);
    }
    //endregion

    //region SINGLETON #######################################/
    private static Model instance;
    private Model(){};
    public static Model getInstance(){
        if (instance == null)
            instance = new Model();
        return instance;
    }
    //endregion


    //region ## GETTER SETTER #######################

    public HashMap<User, UserInQuest> getUserInQuest() {
        return userInQuest;
    }

    public void setUserInQuest(HashMap<User, UserInQuest> userInQuest) {
        this.userInQuest = userInQuest;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<Integer, User> users) {
        this.users = users;
    }

    public HashMap<Integer, Quest> getQuests() {
        return quests;
    }

    public void setQuests(HashMap<Integer, Quest> quests) {
        this.quests = quests;
    }

    //endregion
}
