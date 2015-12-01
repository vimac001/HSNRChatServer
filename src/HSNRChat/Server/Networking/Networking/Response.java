package HSNRChat.Server.Networking.Networking;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class Response {

    ArrayList<Byte> data;
    ResponseStatus status;

    public Response(ServerFunction fnc) {
        data = new ArrayList<>();
        data.add(fnc.getId());
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void appendValue(byte[] data) {
        for (byte bt: data) {
            this.data.add(bt);
        }
    }

    public void appendValue(String str) {
        byte[] data = str.getBytes(Charset.defaultCharset());
        this.appendValue(data.length);
        this.appendValue(data);
    }

    public void appendValue(byte bt) {
        this.data.add(bt);
    }

    public void appendValue(short st) {
        this.data.add((byte)(st & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
    }

    public void appendValue(int st) {
        this.data.add((byte)(st & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
    }

    public void appendValue(long st) {
        this.data.add((byte)(st & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
        this.data.add((byte)((st >> 8) & 0xff));
    }

    public byte[] getBytes() {
        this.data.add(this.status.getId());

        byte[] bts = new byte[this.data.size()];

        int i = 0;
        for(byte bt:this.data) {
            bts[i++] = bt;
        }

        return bts;
    }
}
