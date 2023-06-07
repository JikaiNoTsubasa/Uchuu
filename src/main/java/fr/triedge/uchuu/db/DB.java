package fr.triedge.uchuu.db;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.model.*;
import fr.triedge.uchuu.utils.Utils;

import java.sql.*;
import java.util.ArrayList;

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
        connection = DriverManager.getConnection("jdbc:mysql://"+host+"/uchuu?autoReconnect=true","uchuu",pwd.getDecrypted());
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
            Quest q = getQuest(res.getInt("quest_id"));
            if (q == null)
                continue;
            quests.add(q);
        }
        res.close();
        stmt.close();
        return quests;
    }

    public Quest getQuest(int id) throws SQLException {
        Quest q = null;
        String sql = "select * from quest where quest_id=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            q = new Quest();
            q.setId(res.getInt("quest_id"));
            q.setName(res.getString("quest_name"));
            q.setDescription(res.getString("quest_description"));
            q.setLevel(res.getInt("quest_level"));
            q.setDuration(res.getInt("quest_duration_min"));
            q.setXp(res.getInt("quest_xp"));
            q.setDrops(getDropsForQuest(q.getId()));
            q.setRepeatable(res.getBoolean("quest_repeatable"));
        }
        res.close();
        stmt.close();
        return q;
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

    public Item getItem(int id) throws SQLException {
        String sql = "select * from item where item_id=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet res = stmt.executeQuery();
        Item i = null;
        while (res.next()){
            i = new Item();
            i.setId(res.getInt("item_id"));
            i.setName(res.getString("item_name"));
            i.setValue(res.getFloat("item_value"));
            i.setDescription(res.getString("item_description"));
            i.setImg(res.getString("item_img"));
        }
        res.close();
        stmt.close();
        return i;
    }

    public boolean startQuestForUser(int userId, int questId) throws SQLException {
        Quest quest = getQuest(questId);//Model.getInstance().getQuest(questId);
        if (quest == null)
            throw new RuntimeException("Quest cannot be null");
        String sql = "select * from user_quest where uq_user=? order by uq_order asc";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet res = stmt.executeQuery();
        boolean isQuestAlreadyRunning = false;
        while (res.next()){
            int qId = res.getInt("uq_quest");
            if (questId == qId)
                isQuestAlreadyRunning = true;
        }
        res.close();
        stmt.close();

        if (isQuestAlreadyRunning)
            return false;

        String ins = "insert into user_quest(uq_user,uq_quest,uq_order, uq_start_time, uq_end_time)values(?,?,?,?,?)";
        stmt = getConnection().prepareStatement(ins);
        stmt.setInt(1, userId);
        stmt.setInt(2, questId);
        stmt.setInt(3, 0);

        long currentMillis = System.currentTimeMillis();
        long millis = currentMillis + (quest.getDuration() * 1000*60);
        stmt.setTimestamp(4, new Timestamp(currentMillis));
        stmt.setTimestamp(5, new Timestamp(millis));

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

    public RunningQuest getRunningQuest(int userId, int questId) throws SQLException {
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

    public RunningQuest getRunningQuest(int userId) throws SQLException {
        RunningQuest rq = null;
        String sql = "select * from user_quest where uq_user=? order by uq_order asc";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1,userId);
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

    public void removeRunningQuest(int userId, int questId) throws SQLException{
        String sql = "delete from user_quest where uq_user=? and uq_quest=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2,questId);
        stmt.executeUpdate();
        stmt.close();
    }

    public QuestReport validateQuest(int userId, Quest quest) throws SQLException {
        if (quest == null)
            return null;

        QuestReport report = new QuestReport();
        addXp(userId, quest.getXp());
        report.setXp(quest.getXp());
        for (Drop d : quest.getDrops()){
            float chance = d.getChance();
            float rnd = Utils.getRandomNumber(0f, 100f);
            if (rnd <= chance){
                // Get drop
                int amount = Utils.getRandomNumber(d.getMin(), d.getMax());
                if (amount > 0){
                    addToInventory(userId, d.getItem().getId(), amount);
                    report.add(d.getItem(), amount);
                }else{
                    report.add(d.getItem(), 0);
                }
            }
        }

        removeRunningQuest(userId, quest.getId());
        return report;
    }

    public void addXp(int userId, int xp) throws SQLException {
        String sql = "select * from user where user_id=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet res = stmt.executeQuery();
        if (res.next()){
            int uxp = res.getInt("user_xp");
            int ulevel = res.getInt("user_level");
            int nxp = Utils.getNextLevelXP(ulevel);
            uxp += xp;
            if (uxp >= nxp){
                ulevel += 1;
                uxp = uxp - nxp;
            }
            String sqlUp = "update user set user_level=?, user_xp=? where user_id=?";
            stmt.close();
            stmt = getConnection().prepareStatement(sqlUp);
            stmt.setInt(1, ulevel);
            stmt.setInt(2, uxp);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
        res.close();
        stmt.close();
    }

    public void addToInventory(int userId, int itemId, int amount) throws SQLException {
        // Check if item already in inventory
        String sqlInInv = "select * from inventory where inv_user=? and inv_item=?";
        PreparedStatement stmt = getConnection().prepareStatement(sqlInInv);
        stmt.setInt(1, userId);
        stmt.setInt(2, itemId);

        InventoryItem invItem = null;
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            invItem = new InventoryItem();
            invItem.setId(res.getInt("inv_id"));
            invItem.setUserId(res.getInt("inv_user"));
            invItem.setItemId(res.getInt("inv_item"));
            invItem.setAmount(res.getInt("inv_amount"));
        }
        res.close();
        stmt.close();

        if (invItem != null){
            String sqlUpdate = "update inventory set inv_amount=inv_amount+? where inv_user=? and inv_item=?";
            stmt = getConnection().prepareStatement(sqlUpdate);
            stmt.setInt(1, amount);
            stmt.setInt(2, userId);
            stmt.setInt(3, itemId);
            stmt.executeUpdate();
            stmt.close();
        }else{
            String sqlInsert = "insert into inventory(inv_user,inv_item,inv_amount)values(?,?,?)";
            stmt = getConnection().prepareStatement(sqlInsert);
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.setInt(3, amount);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public Inventory getInventory(User user) throws SQLException {
        Inventory inv = new Inventory();
        String sql = "select * from inventory where inv_user=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, user.getId());
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            InventoryRow i = new InventoryRow();
            Item item = getItem(res.getInt("inv_item"));
            i.setItem(item);
            i.setAmount(res.getInt("inv_amount"));
            inv.getItems().add(i);
        }
        res.close();
        stmt.close();
        return inv;
    }
}
