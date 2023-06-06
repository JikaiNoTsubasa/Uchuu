package fr.triedge.uchuu.model;

import fr.triedge.uchuu.db.DB;

import java.sql.SQLException;
import java.util.ArrayList;

public class Model {

    private static Model instance;
    private ArrayList<Quest> quests;

    private Model(){}

    public static Model getInstance(){
        if (instance == null){
            instance = new Model();
        }
        return instance;
    }

    /**
     * Loads quests in memory if not yet done, then returns the list
     * @return The list of cached quests
     * @throws SQLException
     */
    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public void reloadInMemoryData() throws SQLException {
        quests = DB.getInstance().getAllQuests();
    }

    public Quest getQuest(int id)  {
        return quests.stream().filter(q -> q.getId() == id).findFirst().orElse(null);
    }

}
