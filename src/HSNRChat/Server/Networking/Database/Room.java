package HSNRChat.Server.Networking.Database;

import HSNRChat.Server.Networking.Database.Exceptions.RoomNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Room {

    protected short id;
    protected String name;

    protected static final String TableName = "room";

    protected static final String IdColumn = "id";
    protected static final String NameColumn = "name";

    protected static final String InsertRoomSql = "INSERT INTO " + TableName + "(" + NameColumn + ") VALUES (?);";
    protected static final String SelectRoomSql = "SELECT " + NameColumn + " FROM " + TableName + " WHERE " + IdColumn + " = ?;";
    protected static final String SelectRoomsSql = "SELECT * FROM " + TableName + ";";
    protected static final String CountRoomsSql = "SELECT COUNT(*) AS count FROM " + TableName + ";";

    protected Room(short id, String name) {
        this.id = id;
        this.name = name;
    }

    public short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    protected static int count() throws SQLException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Room.CountRoomsSql);

        ResultSet rm = mgr.query(s);
        rm.first();
        int cnt = rm.getInt(1);
        rm.close();
        return cnt;
    }

    public static Room create(String name) throws SQLException {
        PreparedStatement s = DataManager.get().createInsertStatement(Room.InsertRoomSql);
        s.setString(0, name);

        short id = (short)DataManager.get().insert(s);

        return new Room(id, name);
    }

    public static Room load(short id) throws SQLException, RoomNotFoundException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Room.SelectRoomSql);
        s.setShort(0, id);

        ResultSet rm = mgr.query(s);
        if(rm.isBeforeFirst() && rm.isAfterLast())
            throw new RoomNotFoundException();

        rm.first();
        Room room = new Room(id, rm.getString(Room.NameColumn));
        rm.close();
        return room;
    }

    public static Room[] loadAll() throws SQLException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Room.SelectRoomsSql);

        ResultSet rms = mgr.query(s);

        int rooms = Room.count();

        Room[] rm = new Room[rooms];

        rms.first();

        for(int i = 0; i < rooms; i++) {
            rm[i] = new Room(rms.getShort(IdColumn), rms.getString(NameColumn));
            rms.next();
        }

        rms.close();

        return rm;
    }
}
