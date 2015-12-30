package HSNRChat.Server.Networking.Database;

import com.sun.deploy.security.ValidationState;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class Chat {
    public class RoomTransitionReference {
        protected Room room = null;
        protected Transition transition = null;

        public RoomTransitionReference(byte roomId, byte transitionId) {
            this.room = (roomId == 0) ? null : new Room(roomId);
            this.transition = (transitionId == 0) ? null : new Transition(transitionId);
        }

        public RoomTransitionReference(Room room, Transition transition) {
            this.room = room;
            this.transition = transition;
        }

        public Room getRoom() {
            return this.room;
        }

        public Transition getTransition() {
            return this.transition;
        }
    }

    protected short id;
    protected RoomTransitionReference[] references;

    protected static final String TableName = "chat";

    protected static final String IdColumn = "id";
    protected static final String RoomIdColumn = "room_id";
    protected static final String TransitionIdColumn = "transition_id";

    protected static final String InsertChatSql = "INSERT INTO " + TableName + " (" + RoomIdColumn + "," + TransitionIdColumn + ") VALUES (?,?);";

    public Chat(short id) {
        this.id = id;
    }

    protected Chat(short id, RoomTransitionReference[] references) {
        this.id = id;
        this.references = references;
    }


    public short getId() {
        return this.id;
    }

    public RoomTransitionReference[] getRTReferences() {
        return this.references;
    }

    public static Chat create(Room room, Transition transition) throws SQLException {
        PreparedStatement s = DataManager.get().createInsertStatement(Chat.InsertChatSql);
        if(room == null)
            s.setNull(1, Types.TINYINT);
        else
            s.setByte(1, room.getId());

        if(transition == null)
            s.setNull(2, Types.TINYINT);
        else
            s.setByte(2, transition.getId());

        short id = (short)DataManager.get().insert(s);


        //TODO: ...


        return new Chat(id);
    }
}
