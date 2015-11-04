package HSNRChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerHandle implements Runnable {

    private ServerSocket server;
    private List<TalkerHandle> clients;

    public  ServerHandle(ServerSocket server) {
        this.server = server;
    }

    public void broadcast(String msg) {
        for (TalkerHandle client : clients) {
            client.send(msg);
        }
    }

    @Override
    public void run()
    {
        System.out.println("Waiting for clients ... ");

        Socket client;

        do{
            try {
                client = server.accept();
                clients.add(new TalkerHandle(client));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while(true);
    }
}
