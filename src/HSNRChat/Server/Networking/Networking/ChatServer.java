
package HSNRChat.Server.Networking.Networking;

import HSNRChat.Server.Networking.Database.User;
import HSNRChat.Server.Networking.Exceptions.RoomNotFoundException;
import HSNRChat.Server.Networking.Exceptions.ServerErrorException;
import HSNRChat.Server.Networking.Exceptions.UserNotFoundException;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ChatServer implements Runnable {

    public static final int DefaultPort = 1337;

    private ServerSocket server;
    private Thread clientListener;
    private List<ClientCommunicator> clients;
    private boolean running = false;

    public ChatServer() {
        try {
            server = new ServerSocket(DefaultPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(InetAddress bindAddr) {
        try {
            server = new ServerSocket(DefaultPort, -1, bindAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port, int backlog) {
        try {
            server = new ServerSocket(port, backlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatServer(int port, int backlog, InetAddress bindAddr) {
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
        for(ClientCommunicator c : clients) {
            if(c.isAuthenticated() && c.getId() == uid) {
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
            }

        } catch (SQLException e) {
            throw new ServerErrorException();
        }
    }

    public void writeToRoom(long sid, short rid, String message) throws RoomNotFoundException, ServerErrorException, IOException {
        for(ClientCommunicator c : clients) {
            c.sendMessage(sid, rid, message);
        }
    }

    public void onConnectionClosed(ClientCommunicator client) {
        this.clients.remove(client);
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

                clients.add(new ClientCommunicator(client, this));
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }
}
