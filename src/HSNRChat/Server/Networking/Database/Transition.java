package HSNRChat.Server.Networking.Database;

import HSNRChat.Server.Networking.Exceptions.TransitionNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transition {
    protected byte id;
    protected String name;
    protected byte area;
    protected byte semester;
    protected TransitionKind kind;

    protected static final String TableName = "transition";

    protected static final String IdColumn = "id";
    protected static final String NameColumn = "name";
    protected static final String AreaColumn = "area";
    protected static final String SemesterColumn = "semester";
    protected static final String KindColumn = "kind";

    protected static final String InsertTransitionSql = "INSERT INTO " + TableName + "(" + NameColumn + "," + AreaColumn + "," + SemesterColumn + "," + KindColumn + ") VALUES (?,?,?,?);";
    protected static final String SelectTransitionSql = "SELECT " + NameColumn + "," + AreaColumn + "," + SemesterColumn + "," + KindColumn + " FROM " + TableName + " WHERE " + IdColumn + " = ?;";
    protected static final String SelectTransitionsSql = "SELECT * FROM " + TableName + ";";
    protected static final String CountTransitionsSql = "SELECT COUNT(*) AS count FROM " + TableName + ";";

    public Transition(byte id) {
        this.id = id;
    }

    protected Transition(byte id, String name, byte area, byte semester, TransitionKind kind) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.semester = semester;
        this.kind = kind;
    }

    public byte getId() {
        return this.id;
    }

    public void load() throws SQLException, TransitionNotFoundException {
        Transition tr = Transition.load(this.id);
        this.name = tr.getName();
        this.area = tr.getArea();
        this.semester = tr.getSemester();
        this.kind = tr.getKind();
    }

    public String getName() {
        return this.name;
    }

    public byte getArea() { return this.area; }

    public byte getSemester() { return this.semester; }

    public TransitionKind getKind() { return this.kind; }

    protected static int count() throws SQLException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Transition.CountTransitionsSql);

        ResultSet rm = mgr.query(s);
        rm.first();
        int cnt = rm.getInt(1);
        rm.close();
        return cnt;
    }

    public static Transition create(String name, byte area, byte semester, TransitionKind kind) throws SQLException {
        PreparedStatement s = DataManager.get().createInsertStatement(Transition.InsertTransitionSql);
        s.setString(1, name);
        s.setByte(2, area);
        s.setByte(3, semester);
        s.setString(4, kind.toString());

        byte id = (byte)DataManager.get().insert(s);

        return new Transition(id, name, area, semester, kind);
    }

    public static Transition load(byte id) throws SQLException, TransitionNotFoundException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Transition.SelectTransitionSql);
        s.setShort(1, id);

        ResultSet rm = mgr.query(s);
        if(rm.isBeforeFirst() && rm.isAfterLast())
            throw new TransitionNotFoundException();

        rm.first();
        Transition transition = new Transition(id, rm.getString(Transition.NameColumn), rm.getByte(Transition.AreaColumn),
                                                rm.getByte(Transition.SemesterColumn), TransitionKind.fromString(rm.getString(KindColumn)));

        rm.close();
        return transition;
    }

    public static Transition[] loadAll() throws SQLException {
        DataManager mgr = DataManager.get();
        PreparedStatement s = mgr.createStatement(Transition.SelectTransitionsSql);

        ResultSet rts = mgr.query(s);

        int transitions = Transition.count();

        Transition[] rt = new Transition[transitions];

        rts.first();

        for(int i = 0; i < transitions; i++) {
            rt[i] = new Transition(rts.getByte(IdColumn), rts.getString(NameColumn), rts.getByte(AreaColumn), rts.getByte(SemesterColumn), TransitionKind.fromString(rts.getString(KindColumn)));
            rts.next();
        }

        rts.close();

        return rt;
    }
}
