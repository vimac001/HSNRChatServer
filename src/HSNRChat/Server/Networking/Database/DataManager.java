package HSNRChat.Server.Networking.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final String host = "127.0.0.1";
    public static final int port = 3306;
    public static final String dbnm = "hsnrchat";
    public static final String user = "hsnrchat_server";
    public static final String pass = "f6rIgIqex3NaFu4558TAfEXE42hatO";//"4A4eLu2Opiv8Se5ec8cENez4JAtAGa";

    protected static DataManager mgr = null;

    protected Connection con;

    protected DataManager() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbnm + "?useUnicode=true&characterEncoding=utf-8&characterSetResults=utf8", user, pass);
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
