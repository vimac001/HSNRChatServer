package HSNRChat.Server.Networking.Networking;


import HSNRChat.Server.Networking.Exceptions.RoomNotFoundException;
import HSNRChat.Server.Networking.Exceptions.ServerErrorException;
import HSNRChat.Server.Networking.Exceptions.UserNotFoundException;
import HSNRChat.Server.Networking.Database.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class ClientCommunicator implements Runnable {


    private ChatServer server;

    private Socket client;
    private Thread listener;

    private InputStream in;
    private OutputStream out;

    private User user = null;

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
        return this.user.getSSID();
    }
    public long getId() { return this.user.getId(); }

    public boolean isAuthenticated() {
        return (this.user.getSSID() != 0);
    }

    public void sendMessage(long sid, String message) throws IOException {
        Response rsp = new Response(ClientFunction.ReceiveB);
        rsp.appendValue(sid);
        rsp.appendValue(message);

        this.out.write(rsp.getBytes());
    }

    public void sendMessage(long sid, short rid, String message) throws IOException {
        Response rsp = new Response(ClientFunction.ReceiveA);
        rsp.appendValue(sid);
        rsp.appendValue(rid);
        rsp.appendValue(message);

        this.out.write(rsp.getBytes());
    }

    public void onLogin(String user, String pass) throws IOException {
        Response rsp = new Response(ServerFunction.Login);

        if(this.user != null) {
            this.user.logout();
            this.user = null;
        }
        try {
            this.user = User.find(user, pass);
        } catch (SQLException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        } catch (UserNotFoundException e) {
            try {
                this.user = User.create(user, pass, this.client.getLocalAddress().getHostAddress());
            } catch (SQLException e1) {
                rsp.setStatus(ResponseStatus.ServerError);
            }
        }

        if(this.user != null) {
            rsp.setStatus(ResponseStatus.Success);
            rsp.appendValue(this.user.getSSID());
        }

        this.out.write(rsp.getBytes());
    }

    public void onLogout(long ssid) throws IOException {
        if(ssid == this.user.getSSID()) {
            this.user.logout();
            this.user = null;
        }
    }

    public void onNewMessage(long ssid, short room, String message) throws IOException {
        Response rsp = new Response(ServerFunction.SendA);

        if(ssid == this.user.getSSID()) {
            try {
                this.server.writeToRoom(this.user.getId(), room, message);
                rsp.setStatus(ResponseStatus.Success);
            } catch (RoomNotFoundException e) {
                rsp.setStatus(ResponseStatus.RoomNotFound);
            } catch (ServerErrorException e) {
                rsp.setStatus(ResponseStatus.ServerError);
            }
        } else {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        }

        this.out.write(rsp.getBytes());
    }

    public void onNewMessage(long ssid, long user, String message) throws IOException {
        Response rsp = new Response(ServerFunction.SendB);

        if(ssid == this.user.getSSID()) {
            try {
                this.server.writeToUser(this.user.getId(), user, message);
                rsp.setStatus(ResponseStatus.Success);
            } catch (UserNotFoundException e) {
                rsp.setStatus(ResponseStatus.UserNotFound);
            } catch (ServerErrorException e) {
                rsp.setStatus(ResponseStatus.ServerError);
            }
        } else {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        }

        this.out.write(rsp.getBytes());
    }

    public void onResolveUser(long ssid, long user) throws IOException {
        Response rsp = new Response(ServerFunction.ResolveUser);

        if(ssid == this.user.getSSID()) {
            try {
                User u = User.find(user);

                rsp.setStatus(ResponseStatus.Success);
                rsp.appendValue(u.getId());
                rsp.appendValue(u.getUsername());
                rsp.appendValue(u.getDisplayName());

            } catch (SQLException e) {
                rsp.setStatus(ResponseStatus.ServerError);
            } catch (UserNotFoundException e) {
                rsp.setStatus(ResponseStatus.UserNotFound);
            }
        } else {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        }

        this.out.write(rsp.getBytes());
    }


}
