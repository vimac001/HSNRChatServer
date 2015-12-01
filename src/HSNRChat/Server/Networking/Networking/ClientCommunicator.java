package HSNRChat.Server.Networking.Networking;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

public class ClientCommunicator implements Runnable {


    private ChatServer server;

    private Socket client;
    private Thread listener;

    private InputStream in;
    private OutputStream out;

    private long ssid;

    private Semaphore writing;

    public ClientCommunicator(Socket client, ChatServer server) {
        this.server = server;
        this.client = client;

        this.writing = new Semaphore(-1, false);

        try {
            this.in = client.getInputStream();
            this.out = client.getOutputStream();

            this.listener = new Thread(this);
            this.listener.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte readByte() throws IOException {
        byte[] buffer = new byte[1];
        this.in.read(buffer, 0, 1);

        return buffer[0];
    }

    private short readShort() throws IOException {
        byte[] buffer = new byte[Short.SIZE];
        this.in.read(buffer, 0, Short.SIZE);

        short data = buffer[Short.SIZE - 1];
        for(int i = Short.SIZE - 2; i >= 0; i--) {
            data <<= Byte.SIZE;
            data |= buffer[i];
        }

        return data;
    }

    private int readInt() throws IOException {
        byte[] buffer = new byte[Integer.SIZE];
        this.in.read(buffer, 0, Integer.SIZE);

        int data = buffer[Integer.SIZE - 1];
        for(int i = Integer.SIZE - 2; i >= 0; i--) {
            data <<= Byte.SIZE;
            data |= buffer[i];
        }

        return data;
    }

    private long readLong() throws IOException {
        byte[] buffer = new byte[Long.SIZE];
        this.in.read(buffer, 0, Long.SIZE);

        long data = buffer[Long.SIZE - 1];
        for(int i = Long.SIZE - 2; i >= 0; i--) {
            data <<= Byte.SIZE;
            data |= buffer[i];
        }

        return data;
    }

    private String readString() throws IOException {
        int bytesCount = this.readInt();
        byte[] buffer = new byte[bytesCount];
        this.in.read(buffer, 0, bytesCount);

        return new String(buffer, Charset.defaultCharset());
    }

    private ResponseStatus readStatus() throws IOException {
        return ResponseStatus.fromByte(this.readByte());
    }

    private ServerFunction readFunction() throws IOException {
        return ServerFunction.fromByte(this.readByte());
    }



    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted() && client.isConnected()) {
            try {
                ServerFunction fnc = this.readFunction();
                if(fnc != ServerFunction.Undefined) {
                    long ssid;
                    String msg;

                    switch (fnc) {
                        case Login:
                            msg = this.readString();
                            String pass = this.readString();

                            this.onLogin(msg, pass);
                            break;

                        case Logout:
                            ssid = this.readLong();

                            this.onLogout(ssid);
                            break;

                        case SendA:
                            ssid = this.readLong();
                            short roomId = this.readShort();
                            msg = this.readString();

                            this.onNewMessage(ssid, roomId, msg);
                            break;

                        case SendB:
                            ssid = this.readLong();
                            long uid = this.readLong();
                            msg = this.readString();

                            this.onNewMessage(ssid, uid, msg);
                            break;

                        case ResolveUser:
                            ssid = this.readLong();
                            long userId = this.readLong();

                            this.onResolveUser(ssid, userId);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(!client.isConnected())
            server.onConnectionClosed(this);
    }

    public long getSSID() {
        return this.ssid;
    }

    public boolean isAuthenticated() {
        return (this.ssid != 0);
    }

    public void onLogin(String user, String pass) throws IOException {
        Response rsp = new Response(ServerFunction.Login);
        long ssid = 11111;

        rsp.setStatus(ResponseStatus.Success);
        rsp.appendValue(ssid);

        this.out.write(rsp.getBytes());
    }

    public void onLogout(long ssid) throws IOException {
        if(ssid == this.ssid) {
            //TODO: Say logout.
            this.ssid = 0;
        }
    }

    public void onNewMessage(long ssid, short room, String message) throws IOException {

    }

    public void onNewMessage(long ssid, long user, String message) throws IOException {

    }

    public void onResolveUser(long ssid, long user) throws IOException {

    }


}
