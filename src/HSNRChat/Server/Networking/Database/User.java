package HSNRChat.Server.Networking.Database;

import HSNRChat.Server.Networking.Exceptions.UserNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class User {
    protected long id;
    protected String uname;
    protected String fname = null;
    protected String lname = null;
    protected long ssid;
    protected String addr = null;

    protected static final String tableName = "user";

    protected static final String IdColumn = "id";
    protected static final String UNameColumn = "uname";
    protected static final String UPassColumn = "upass";
    protected static final String FNameColumn = "fname";
    protected static final String LNameColumn = "lname";
    protected static final String SSIDColumn = "ssid";
    protected static final String AddrColumn = "ipaddr";

    protected static final String CheckUserIdExistsSql = "SELECT COUNT(" + IdColumn + ") FROM user WHERE " + IdColumn + " = ?;";
    protected static final String CheckSSIDExistsSql = "SELECT COUNT(" + SSIDColumn + ") FROM user WHERE " + SSIDColumn + "= ?;";
    protected static final String InsertUserSql = "INSERT INTO " + tableName + " (" + IdColumn + "," + UNameColumn + "," + UPassColumn + ") VALUES (?,?,SHA1(?));";
    protected static final String InserAndLogintUserSql = "INSERT INTO " + tableName + " (" + IdColumn + "," + UNameColumn + "," + UPassColumn + "," + SSIDColumn + "," + AddrColumn + ") VALUES (?,?,SHA1(?),?,?);";
    protected static final String SelectUserById = "SELECT * FROM " + tableName + " WHERE " + IdColumn + " = ?;";
    protected static final String SelectUserByLogin = "SELECT * FROM " + tableName + " WHERE " + UNameColumn + " LIKE ? AND " + UPassColumn + " LIKE SHA1(?);";

    public User(long id) {
        this.id = id;
    }

    protected User(long id, String uname) {
        this.id = id;
        this.uname = uname;
    }

    protected User(long id, String uname, String fname, String lname, long ssid, String addr) {
        this.id = id;
        this.uname = uname;
        this.fname = fname;
        this.lname = lname;
        this.ssid = ssid;
        this.addr = addr;
    }

    public long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.uname;
    }

    public String getFirstName() {
        return this.fname;
    }

    public String getLastName() {
        return this.lname;
    }

    public String getDisplayName() {
        return this.fname + " " + this.lname;
    }

    public long getSSID() {
        return this.ssid;
    }

    public String getLastIpAddress() {
        return this.addr;
    }

    public void setFirstName(String fname) {
        //TODO: set
    }

    public void setLastName(String lname) {
        //TODO: set
    }

    public long setSSID(String addr) {
        //TODO: set
        return this.ssid;
    }

    public void logout() {
        //TODO: set
    }

    public static boolean idExists(long id) throws SQLException {
        PreparedStatement s = DataManager.get().createStatement(User.CheckUserIdExistsSql);
        s.setLong(1, id);

        ResultSet r = s.executeQuery();
        r.first();
        return (r.getInt(1) > 0);
    }

    protected static boolean ssidExists(long ssid) throws SQLException {
        PreparedStatement s = DataManager.get().createStatement(User.CheckSSIDExistsSql);
        s.setLong(1, ssid);

        ResultSet r = s.executeQuery();
        r.first();
        return (r.getInt(1) > 0);
    }

    protected static long generateId() throws SQLException {
        Random r = new Random();
        long id;
        do {
            do {
                id = r.nextLong();
            } while (id < 100000000000L || id > 999999999999L);
        }while(idExists(id));

        return id;
    }

    protected static long generateSSID() throws SQLException {
        Random r = new Random();
        long ssid;
        do {
            do {
                ssid = r.nextLong();
            } while (ssid < 100000000000L);
        }while(ssidExists(ssid));

        return ssid;
    }

    public static User create(String uname, String upass) throws SQLException {
        PreparedStatement s = DataManager.get().createInsertStatement(User.InsertUserSql);
        long id = User.generateId();
        s.setLong(1, id);
        s.setString(2, uname);
        s.setString(3, upass);

        return new User(id, uname);
    }

    public static User create(String uname, String upass, String addr) throws SQLException {
        PreparedStatement s = DataManager.get().createInsertStatement(User.InserAndLogintUserSql);
        long id = User.generateId();
        long ssid = generateSSID();
        s.setLong(1, id);
        s.setString(2, uname);
        s.setString(3, upass);
        s.setLong(4, ssid);
        s.setString(5, addr);

        return new User(id, uname, null, null, ssid, addr);
    }

    public static User find(long id) throws SQLException, UserNotFoundException {
        PreparedStatement s = DataManager.get().createStatement(User.SelectUserById);
        s.setLong(1, id);

        ResultSet r = s.executeQuery();
        if(r.isBeforeFirst() && r.isAfterLast())
            throw new UserNotFoundException();

        r.first();
        return new User(r.getByte(IdColumn), r.getString(UNameColumn), r.getString(FNameColumn), r.getString(LNameColumn), r.getLong(SSIDColumn), r.getString(AddrColumn));
    }

    public static User find(String uname, String upass) throws SQLException, UserNotFoundException {
        PreparedStatement s = DataManager.get().createStatement(User.SelectUserByLogin);
        s.setString(1, uname);
        s.setString(2, upass);

        ResultSet r = s.executeQuery();
        if(r.isBeforeFirst() && r.isAfterLast())
            throw new UserNotFoundException();

        r.first();
        return new User(r.getByte(IdColumn), r.getString(UNameColumn), r.getString(FNameColumn), r.getString(LNameColumn), r.getLong(SSIDColumn), r.getString(AddrColumn));
    }

}
