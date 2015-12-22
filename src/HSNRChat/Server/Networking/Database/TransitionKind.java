package HSNRChat.Server.Networking.Database;


public enum TransitionKind {
    Undefined((byte)0),
    Bachelor((byte)1),
    Master((byte)2);

    private byte id;

    private TransitionKind(byte id) {
        this.id = id;
    }

    public static TransitionKind fromString(String kind) {
        switch (kind) {
            case "Bachelor":
                return TransitionKind.Bachelor;

            case "Master":
                return TransitionKind.Master;

            default:
                return TransitionKind.Undefined;
        }
    }

}
