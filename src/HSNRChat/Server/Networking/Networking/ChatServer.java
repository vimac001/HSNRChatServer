
package HSNRChat.Server.Networking.Networking;

import HSNRChat.Server.Networking.Database.User;
import HSNRChat.Server.Networking.Exceptions.RoomNotFoundException;
import HSNRChat.Server.Networking.Exceptions.ServerErrorException;
import HSNRChat.Server.Networking.Exceptions.UserNotFoundException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements Runnable {

    public static final int DefaultPort = 1338;

    private ServerSocket server;
    private Thread clientListener;
    private List<ClientHandle> clients;
    private boolean running = false;

    public ChatServer() {
        this.clients = new ArrayList<>();

        try {
            server = new ServerSocket(DefaultPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(InetAddress bindAddr) {
        this.clients = new ArrayList<>();

        try {
            server = new ServerSocket(DefaultPort, -1, bindAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port) {
        this.clients = new ArrayList<>();

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port, int backlog) {
        this.clients = new ArrayList<>();

        try {
            server = new ServerSocket(port, backlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port, int backlog, InetAddress bindAddr) {
        this.clients = new ArrayList<>();

        try {
            server = new ServerSocket(port, backlog, bindAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void writeToUser(long sid, long uid, String message) throws UserNotFoundException, ServerErrorException, IOException {
        for(ClientHandle c : clients) {
            if (c.isAuthenticated() && c.getUser().getId() == uid) {
                c.sendMessage(sid, message);
                return;
            }
        }

        try {
            User u = User.find(uid);

            if(u == null) {
                throw new UserNotFoundException();
            } else {
                //TODO: User is offline
                //Vorschlag: Eine offline history.
                throw new ServerErrorException();
            }

        } catch (SQLException e) {
            throw new ServerErrorException();
        }
    }

    public void writeToRoom(long sid, short rid, String message) throws RoomNotFoundException, ServerErrorException, IOException {
       for(ClientHandle c : clients) {
            c.sendMessage(sid, rid, message);
       }
    }

    public void onConnectionClosed(ClientHandle client) {
        System.out.print("Connection closed @(");
        System.out.print(client.getIpAddrString());
        System.out.print(':');
        System.out.print(client.getPort());
        System.out.println(")");

        this.clients.remove(client);
        Thread.currentThread().interrupt();
    }

    @Override
    public void run()
    {
        if(!isRunning()) {
            clientListener = new Thread(this);
            running = true;

            System.out.print("Listening @(");
            System.out.print(server.getInetAddress().getHostAddress());
            System.out.print(':');
            System.out.print(server.getLocalPort());
            System.out.println(") ... ");

            clientListener.run();

            return;
        }

        Socket client;

        while (!Thread.currentThread().isInterrupted() && server.isBound()) {
            try {
                client = server.accept();
                System.out.print("Incomming connection @(");
                System.out.print(client.getInetAddress().getHostAddress());
                System.out.print(':');
                System.out.print(client.getPort());
                System.out.println(")");

                this.clients.add(new ClientHandle(client, this));
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }
}
