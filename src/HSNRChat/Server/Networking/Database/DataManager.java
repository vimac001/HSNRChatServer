package HSNRChat.Server.Networking.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final String host = "192.168.1.1";
    public static final int port = 3306;
    public static final String dbnm = "hsnrchat";
    public static final String user = "hsnrchat_server";
    public static final String pass = "4A4eLu2Opiv8Se5ec8cENez4JAtAGa";

    protected static DataManager mgr = null;

    protected Connection con;

    protected DataManager() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbnm, user, pass);
        System.out.println("Successfully connected with database server.");
    }

    public PreparedStatement createStatement(String sql) throws SQLException {
        return this.con.prepareStatement(sql);
    }

    public PreparedStatement createInsertStatement(String sql) throws SQLException {
        return this.con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public int insert(PreparedStatement statement) throws SQLException {
        int insertId = statement.executeUpdate();
        statement.close();
        return insertId;
    }

    public ResultSet query(PreparedStatement statement) throws SQLException {
        ResultSet data = statement.executeQuery();
        //statement.close();
        return data;
    }

    public boolean isConnected() throws SQLException {
        return !con.isClosed();
    }

    public static DataManager get() throws SQLException {
        if(mgr == null) {
            mgr = new DataManager();
        }

        return mgr;
    }

    public static void connect() throws SQLException {
        if(mgr == null || !mgr.isConnected() ) {
            mgr = new DataManager();
        }
    }
}
