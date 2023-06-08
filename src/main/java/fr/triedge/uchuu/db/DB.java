package fr.triedge.uchuu.db;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.model.*;
import fr.triedge.uchuu.utils.Utils;
import jdk.jshell.execution.Util;

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
        user.setAdmin(res.getBoolean("user_admin"));
        return user;
    }

    public ArrayList<Quest> getAllQuests(User user) throws SQLException {
        ArrayList<Integer> doneIds = getQuestsDoneIdsForUser(user.getId());
        ArrayList<Quest> quests = new ArrayList<>();
        String sql = "select * from quest order by quest_level";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            int questId = res.getInt("quest_id");
            if (doneIds.contains(questId)){
                continue;
            }
            Quest q = getQuest(questId);
            if (q == null) {
                continue;
            }
            quests.add(q);
        }
        res.close();
        stmt.close();
        return quests;
    }

    public ArrayList<Integer> getQuestsDoneIdsForUser(int userId) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<>();
        String sql = "select * from quest_done where qd_user=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            int id = res.getInt("qd_quest");
            ids.add(id);
        }
        res.close();
        stmt.close();
        return ids;
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
            q.setPlan(getPlan(res.getInt("quest_plan")));
        }
        res.close();
        stmt.close();
        return q;
    }

    public Plan getPlan(int planId) throws SQLException {
        Plan p = null;
        Building b = getBuilding(planId);
        if (b != null){
            p = new Plan();
            p.setBuilding(b);
        }
        return p;
    }

    public ArrayList<Recipe> getRecipeForBuilding(int buildingId, int level) throws SQLException {
        ArrayList<Recipe> list = new ArrayList<>();
        String sql = "select * from building_level where bl_building=? and bl_level=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, buildingId);
        stmt.setInt(2, level);
        ResultSet res = stmt.executeQuery();
        while(res.next()){
            list.add(getRecipe(res.getInt("bl_recipe")));
        }
        return list;
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
            i.setForgeable(res.getBoolean("item_forgeable"));
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

        if (!quest.isRepeatable()){
            RunningQuest rq = getRunningQuest(userId, quest.getId());
            setQuestDone(userId, quest.getId(), rq.getStartTime(), rq.getEndTime());
            if (quest.getPlan() != null){
                addPlanToUser(userId, quest.getPlan().getBuilding().getId());
            }
        }
        removeRunningQuest(userId, quest.getId());
        return report;
    }

    public void setQuestDone(int userId, int questId, long startTime, long endTime) throws SQLException {
        String sql = "insert into quest_done(qd_user,qd_quest,qd_start_time,qd_end_time)values(?,?,?,?)";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, questId);
        stmt.setTimestamp(3, new Timestamp(startTime));
        stmt.setTimestamp(4, new Timestamp(endTime));
        stmt.executeUpdate();
        stmt.close();
    }

    public void addPlanToUser(int userId, int buildingId) throws SQLException {
        String sql = "insert into user_building(ub_user,ub_building)values(?,?)";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, buildingId);
        stmt.executeUpdate();
        stmt.close();
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

    public ArrayList<UserBuilding> getUserAvailableBuildings(User user) throws SQLException {
        ArrayList<UserBuilding> bs = new ArrayList<>();
        String sql = "select * from user_building where ub_user=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, user.getId());
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            UserBuilding ub = new UserBuilding();
            ub.setLevel(res.getInt("ub_level"));
            ub.setStarted(res.getBoolean("ub_started"));
            ub.setBuilding(getBuilding(res.getInt("ub_building")));
            bs.add(ub);
        }
        res.close();
        stmt.close();
        return bs;
    }

    public Building getBuilding(int buildingId) throws SQLException {
        Building b = null;
        String sql = "select * from building where building_id=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, buildingId);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            b = new Building();
            b.setName(res.getString("building_name"));
            b.setId(res.getInt("building_id"));
            b.setImg(res.getString("building_img"));
            b.setDescription(res.getString("building_description"));
            b.setLevels(getBuildingLevel(b.getId()));
        }
        res.close();
        stmt.close();
        return b;
    }

    public ArrayList<Building> getBuildings() throws SQLException {
        ArrayList<Building> bs = new ArrayList<>();
        String sql = "select * from building";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            bs.add(getBuilding(res.getInt("building_id")));
        }
        res.close();
        stmt.close();
        return bs;
    }

    public ArrayList<BuildingLevel> getBuildingLevel(int buildingId) throws SQLException {
        ArrayList<BuildingLevel> levels = new ArrayList<>();
        String sql = "select * from building_level where bl_building=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, buildingId);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            BuildingLevel bl = new BuildingLevel();
            bl.setLevel(res.getInt("bl_level"));
            bl.setRecipe(getRecipe(res.getInt("bl_recipe")));
            levels.add(bl);
        }
        res.close();
        stmt.close();
        return levels;
    }

    public Recipe getRecipe(int id) throws SQLException {
        Recipe r = new Recipe();
        String sql = "select * from recipe_item join recipe on recipe_id=ri_recipe where ri_recipe=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            r.setId(res.getInt("recipe_id"));
            r.setName(res.getString("recipe_name"));
            RecipeItem ri = new RecipeItem();
            ri.setId(res.getInt("ri_id"));
            ri.setAmount(res.getInt("ri_amount"));
            ri.setItem(getItem(res.getInt("ri_item")));
            r.getRecipeItems().add(ri);
        }
        res.close();
        stmt.close();
        return r;
    }

    public ArrayList<AdminUserEntry> adminGetUsers() throws SQLException {
        ArrayList<AdminUserEntry> users = new ArrayList<>();
        String sql = "select * from user order by user_level desc";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        ResultSet res = stmt.executeQuery();
        while (res.next()){
            AdminUserEntry a = new AdminUserEntry();
            User u = getUser(res.getString("user_name"));
            a.setUser(getUser(res.getString("user_name")));
            RunningQuest q = getRunningQuest(u.getId());
            if (q!=null){
                Quest qq = getQuest(q.getQuestId());
                a.setCurrentQuest(qq);
                a.setRunningQuest(q);
            }
            a.setNextLevelXP(Utils.getNextLevelXP(u.getLevel()));
            a.setNextLevelPercent(Utils.getNextLevelPercent(u.getXp(), u.getLevel()));

            a.setBuildings(getUserAvailableBuildings(u));

            users.add(a);
        }
        res.close();
        stmt.close();
        return users;
    }

    public void adminFinishQuest(int userId, int questId) throws SQLException {
        String sql = "update user_quest set uq_end_time=uq_start_time where uq_user=? and uq_quest=?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, questId);
        stmt.executeUpdate();
        stmt.close();
    }
}
