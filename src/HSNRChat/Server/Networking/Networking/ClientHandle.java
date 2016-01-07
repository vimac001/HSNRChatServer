package HSNRChat.Server.Networking.Networking;

import HSNRChat.Server.Networking.Database.User;
import HSNRChat.Server.Networking.Exceptions.InvalidSSIDException;
import HSNRChat.Server.Networking.Exceptions.RoomNotFoundException;
import HSNRChat.Server.Networking.Exceptions.ServerErrorException;
import HSNRChat.Server.Networking.Exceptions.UserNotFoundException;
import HSNRChat.Server.Networking.Networking.Streaming.StructuredInputStream;
import HSNRChat.Server.Networking.Networking.Streaming.StructuredOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandle {
    private Socket sock;
    private ChatServer svr;

    InputStream in;
    OutputStream out;

    StructuredInputStream is;
    StructuredOutputStream os;

    private ClientListener listener;
    private Thread tListener;

    private User user = null;

    public ClientHandle(Socket client, ChatServer server) throws IOException {
        this.sock = client;
        this.svr = server;

        if(this.sock.isConnected()) {
            this.in = this.sock.getInputStream();
            this.out = this.sock.getOutputStream();

            this.is = new StructuredInputStream(this.in);
            this.os = new StructuredOutputStream(this.out);

            this.listener = new ClientListener(this.is, this);
            this.tListener = new Thread(this.listener);
            this.tListener.start();
        }
    }

    public boolean isConnected() {
        return this.sock.isConnected();
    }

    public String getIpAddrString() {
        return this.sock.getLocalAddress().getHostAddress();
    }

    public int getPort() {
        return this.sock.getPort();
    }

    public int getLocalPort() {
        return this.sock.getLocalPort();
    }

    public boolean isAuthenticated() {
        return this.user != null;
    }

    public User getUser() {
        return this.user;
    }

    public Socket getSocket() {
        return this.sock;
    }

    public boolean authenticate(long ssid) throws InvalidSSIDException, ServerErrorException {
        if(!this.isAuthenticated()) {
            try {
                this.user = User.find(ssid, this.getIpAddrString());
            } catch (SQLException e) {
                throw new ServerErrorException();
            } catch (UserNotFoundException e) {
                throw new InvalidSSIDException();
            }
        } else {
            if(this.user.getSSID() != ssid)
                throw new InvalidSSIDException();
        }

        return this.user != null;
    }

    public void logout() {
        if(this.user != null) {
            try {
                this.user.logout();
            } catch (SQLException e) {
                // Ignore
            } finally {
                this.user = null;
            }
        }
    }

    public void sendMessage(long sid, String message) throws IOException {
        Response rsp = new Response(ClientFunction.ReceiveB);
        rsp.appendValue(sid);
        rsp.appendValue(message);

        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void sendMessage(long sid, short rid, String message) throws IOException {
        Response rsp = new Response(ClientFunction.ReceiveA);
        rsp.appendValue(sid);
        rsp.appendValue(rid);
        rsp.appendValue(message);

        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void onNewCall(ServerFunction fnc) {
        long ssid;
        String msg;

        try {
            switch (fnc) {
                case Login:
                    msg = this.is.readUTF();
                    String pass = this.is.readUTF();

                    this.onLogin(msg, pass);
                    break;

                case Logout:
                    ssid = this.is.readLong();

                    this.onLogout(ssid);
                    break;

                case SendA:
                    ssid = this.is.readLong();
                    short roomId = this.is.readShort();
                    msg = this.is.readUTF();

                    this.onNewMessage(ssid, roomId, msg);
                    break;

                case SendB:
                    ssid = this.is.readLong();
                    long uid = this.is.readLong();
                    msg = this.is.readUTF();

                    this.onNewMessage(ssid, uid, msg);
                    break;

                case ResolveUser:
                    ssid = this.is.readLong();
                    long userId = this.is.readLong();

                    this.onResolveUser(ssid, userId);
                    break;
            }
        } catch (IOException e) {
            this.onClosed();
        }
    }

    public void onLogin(String user, String pass) throws IOException {
        Response rsp = new Response(ServerFunction.Login);

        try {
            this.logout();

            try {
                this.user = User.find(user, pass);
            } catch (UserNotFoundException e) {
                this.user = User.create(user, pass, this.getIpAddrString());
            }

            if(this.user != null) {
                rsp.setStatus(ResponseStatus.Success);
                rsp.appendValue(this.user.getSSID());
            } else {
                rsp.setStatus(ResponseStatus.UserNotFound);
            }
        } catch (SQLException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        }


        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void onLogout(long ssid)  {
        if(this.user.getSSID() == ssid) {
            this.logout();
        }
    }

    public void onNewMessage(long ssid, short roomId, String message) throws IOException {
        Response rsp = new Response(ServerFunction.SendA);

        try {
            if(this.authenticate(ssid)) {
                this.svr.writeToRoom(this.user.getId(), roomId, message);
                rsp.setStatus(ResponseStatus.Success);
            } else {
                //Anomalie, this case will never happen.
                rsp.setStatus(ResponseStatus.ServerError);
                System.err.println("Unexpected code block reached in onNewMessageA.");
            }
        } catch (InvalidSSIDException e) {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        } catch (ServerErrorException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        } catch (RoomNotFoundException e) {
            rsp.setStatus(ResponseStatus.RoomNotFound);
        }

        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void onNewMessage(long ssid, long uId, String message) throws IOException {
        Response rsp = new Response(ServerFunction.SendB);

        try {
            if(this.authenticate(ssid)) {
                this.svr.writeToUser(this.user.getId(), uId, message);
                rsp.setStatus(ResponseStatus.Success);
            } else {
                //Anomalie, this case will never happen.
                rsp.setStatus(ResponseStatus.ServerError);
                System.err.println("Unexpected code block reached in onNewMessageB.");
            }
        } catch (InvalidSSIDException e) {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        } catch (ServerErrorException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        } catch (UserNotFoundException e) {
            rsp.setStatus(ResponseStatus.UserNotFound);
        }

        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void onResolveUser(long ssid, long uid) throws IOException {
        Response rsp = new Response(ServerFunction.ResolveUser);

        try {
            if(this.authenticate(ssid)) {
                User u = User.find(uid);
                rsp.appendValue(u.getId());
                rsp.appendValue(u.getUsername());
                rsp.appendValue(u.getDisplayName());

                rsp.setStatus(ResponseStatus.Success);
            }
        } catch (InvalidSSIDException e) {
            rsp.setStatus(ResponseStatus.InvalidSSID);
        } catch (ServerErrorException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        } catch (SQLException e) {
            rsp.setStatus(ResponseStatus.ServerError);
        } catch (UserNotFoundException e) {
            rsp.setStatus(ResponseStatus.UserNotFound);
        }

        this.os.write(rsp.getBytes());
        this.os.flush();
    }

    public void onClosed() {
        this.svr.onConnectionClosed(this);
    }
}
