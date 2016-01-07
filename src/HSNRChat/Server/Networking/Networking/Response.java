package HSNRChat.Server.Networking.Networking;

import HSNRChat.Server.Networking.Networking.Streaming.StructuredInputStream;
import HSNRChat.Server.Networking.Networking.Streaming.StructuredOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Response {

    OutputStream out;
    StructuredOutputStream os;

    ArrayList<Byte> data;
    ResponseStatus status;

    private boolean isClientFunction = false;

    public Response(ServerFunction fnc) {
        data = new ArrayList<>();
        this.out = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                data.add((byte)oneByte);
            }
        };

        this.os = new StructuredOutputStream(this.out);
        try {
            this.os.writeFunction(fnc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response(ClientFunction fnc) {
        data = new ArrayList<>();
        this.out = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                data.add((byte)oneByte);
            }
        };
        this.isClientFunction = true;

        this.os = new StructuredOutputStream(this.out);
        try {
            this.os.writeFunction(fnc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void appendValue(byte[] data) {
        try {
            this.os.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendValue(String str) {
        try {
            this.os.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendValue(byte bt) {
        try {
            this.os.writeByte(bt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendValue(short st) {
        try {
            this.os.writeShort(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendValue(int st) {
        try {
            this.os.writeInt(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendValue(long st) {
        try {
            this.os.writeLong(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes() {
        int datLen = this.data.size();
        if(!this.isClientFunction)
            datLen++;

        byte[] bts = new byte[datLen];

        int i = 0;
        if(!this.isClientFunction) {
            bts[i++] = data.get(0);
            bts[i++] = this.status.getId();

            data.remove(0);
        }

        for(byte bt:this.data) {
            bts[i++] = bt;
        }

        return bts;
    }
}
