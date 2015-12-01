package HSNRChat.Server.Networking.Networking;

public enum ResponseStatus {
    Undefined((byte)0x00),
    ServerError((byte)0xf1),
    UserNotFound((byte)0xf2),
    RoomNotFound((byte)0xf3),
    InvalidSSID((byte)0xf4),
    Success((byte)0x01);


    private final byte id;

    ResponseStatus(byte id) {
        this.id = id;
    }

    public static ResponseStatus fromByte(byte id) {
        for (ResponseStatus stat:ResponseStatus.values()) {
            if(stat.getId() == id)
                return stat;
        }

        return ResponseStatus.Undefined;
    }

    public byte getId() {
        return this.id;
    }
}
