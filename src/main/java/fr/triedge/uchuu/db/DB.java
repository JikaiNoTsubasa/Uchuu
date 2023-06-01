package fr.triedge.uchuu.db;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.model.User;

import java.sql.*;

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
        return user;
    }
}
