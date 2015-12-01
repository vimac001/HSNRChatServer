package HSNRChat.Server.Networking.Networking;


public enum ServerFunction {
    Undefined((byte)0),
    Login((byte)1),
    Logout((byte)2),
    SendA((byte)5),
    SendB((byte)6),
    ResolveUser((byte)10);

    private final byte id;

    ServerFunction(byte id) {
        this.id = id;
    }

    public static ServerFunction fromByte(byte id) {
        for (ServerFunction fnc:ServerFunction.values()) {
            if(fnc.getId() == id)
                return fnc;
        }

        return ServerFunction.Undefined;
    }

    public byte getId() {
        return this.id;
    }
}
