package fr.triedge.uchuu.db;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DB {

    private static DB instance;
    private Connection connection;

    private DB(){}

    public static DB getInstance(){
        if (instance == null){
            instance = new DB();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()){
            resetConnection();
        }
        return connection;
    }

    public void resetConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        SPassword pwd = new SPassword("c2JpLSRiaXVzZXJTIzg4");
        String host = "localhost";
        if (System.getProperty("host") != null){
            host = System.getProperty("host");
        }
        connection = DriverManager.getConnection("jdbc:mysql://"+host+"/uchuu","uchuu",pwd.getDecrypted());
    }

    public void createUser(String username, String password) throws SQLException {
        String sql = "insert into user(user_name, user_password)values(?,?)";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, new SPassword(password).getEncrypted());
        stmt.executeUpdate();
    }

    public User getUser(String username, String password) throws SQLException {
        User user = null;
        SPassword pwd = new SPassword(password);
        String sql = "select * from user where user_name=? and user_password=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, pwd.getEncrypted());
        ResultSet res = stmt.executeQuery();
        if (res.next()){
            user = loadUser(res);
        }
        res.close();
        stmt.close();

        return user;
    }

    public User getUser(String username) throws SQLException {
        User user = null;
        String sql = "select * from user where user_name=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet res = stmt.executeQuery();
        if (res.next()){
            user = loadUser(res);
        }
        res.close();
        stmt.close();

        return user;
    }

    private User loadUser(ResultSet res) throws SQLException {
        User user = new User();
        user.setId(res.getInt("user_id"));
        user.setUsername(res.getString("user_name"));
        user.setLevel(res.getInt("user_level"));
        user.setXp(res.getInt("user_xp"));
        return user;
    }

    public ArrayList<Quest> getAllQuests() throws SQLException {
        ArrayList<Quest> quests = new ArrayList<>();
        String sql = "select * from quest order by quest_level";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            Quest q = new Quest();
            q.setId(res.getInt("quest_id"));
            q.setName(res.getString("quest_name"));
            q.setDescription(res.getString("quest_description"));
            q.setLevel(res.getInt("quest_level"));
            q.setDuration(res.getInt("quest_duration_min"));
            q.setXp(res.getInt("quest_xp"));
            q.setDrops(getDropsForQuest(q.getId()));
            quests.add(q);
        }
        res.close();
        stmt.close();
        return quests;
    }

    public ArrayList<Drop> getDropsForQuest(int questId) throws SQLException {
        ArrayList<Drop> drops = new ArrayList<>();
        String sql = "select * from quest_drop left join item on drop_item=item_id where drop_quest=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, questId);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            Drop d = new Drop();
            Item i = new Item();
            i.setId(res.getInt("item_id"));
            i.setName(res.getString("item_name"));
            i.setValue(res.getFloat("item_value"));
            i.setDescription(res.getString("item_description"));
            i.setImg(res.getString("item_img"));

            d.setItem(i);
            d.setId(res.getInt("drop_id"));
            d.setChance(res.getFloat("drop_chance"));
            d.setMin(res.getInt("drop_amount_min"));
            d.setMax(res.getInt("drop_amount_max"));
            drops.add(d);
        }
        res.close();
        stmt.close();
        return drops;
    }

    public boolean startQuestForUser(int userId, int questId) throws SQLException {
        Quest quest = Model.getInstance().getQuest(questId);
        if (quest == null)
            throw new RuntimeException("Quest cannot be null");
        String sql = "select * from user_quest where uq_user=? order by uq_order asc";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet res = stmt.executeQuery();
        int order = 0;
        boolean isQuestalreadRunning = false;
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        while (res.next()){
            order = res.getInt("uq_order") + 1;
            ts = res.getTimestamp("uq_end_time");
            int qId = res.getInt("uq_quest");
            if (questId == qId)
                isQuestalreadRunning = true;
        }
        res.close();
        stmt.close();

        if (isQuestalreadRunning)
            return false;

        String ins = "insert into user_quest(uq_user,uq_quest,uq_order,uq_end_time)values(?,?,?,?)";
        stmt = getConnection().prepareStatement(ins);
        stmt.setInt(1, userId);
        stmt.setInt(2, questId);
        stmt.setInt(3, order);

        long mins = TimeUnit.MILLISECONDS.toMinutes(ts.getTime()) + quest.getDuration();
        long millis = TimeUnit.MINUTES.toMillis(mins);
        stmt.setTimestamp(4, new Timestamp(millis));

        stmt.executeUpdate();
        stmt.close();

        return true;
    }

    public boolean isUserInQuest(int userId) throws SQLException {
        String sql = "select count(*) from user_quest where uq_user=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        int count = 0;
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            count = res.getInt(1);
        }
        res.close();
        stmt.close();
        return count > 0;
    }

    public boolean isUserInQuest(User user) throws SQLException {
        if (user == null)
            throw new RuntimeException("User can't be null");
        return isUserInQuest(user.getId());
    }

    public RunningQuest getRuningQuest(int userId, int questId) throws SQLException {
        RunningQuest rq = null;
        String sql = "select * from user_quest where uq_user=? and uq_quest=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1,userId);
        stmt.setInt(2,questId);
        ResultSet res = stmt.executeQuery();
        if (res.next()){
            rq = new RunningQuest();
            rq.setUserId(res.getInt("uq_user"));
            rq.setQuestId(res.getInt("uq_quest"));
            rq.setOrder(res.getInt("uq_order"));
            rq.setStartTime(res.getTimestamp("uq_start_time").getTime());
            rq.setEndTime(res.getTimestamp("uq_end_time").getTime());
        }
        res.close();
        stmt.close();
        return rq;
    }

}
