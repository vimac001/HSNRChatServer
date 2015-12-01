package HSNRChat.Server.Networking.Networking;

public enum ClientFunction {
    Undefined((byte)0),
    ReceiveA((byte)105),
    ReceiveB((byte)106);

    private final byte id;

    ClientFunction(byte id) {
        this.id = id;
    }

    public static ClientFunction fromByte(byte id) {
        for (ClientFunction fnc:ClientFunction.values()) {
            if(fnc.getId() == id)
                return fnc;
        }

        return ClientFunction.Undefined;
    }

    public byte getId() {
        return this.id;
    }
}