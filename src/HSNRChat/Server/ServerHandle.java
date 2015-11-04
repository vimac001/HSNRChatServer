package HSNRChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandle implements Runnable {

    private ServerSocket server;
    private List<TalkerHandle> clients;

    public  ServerHandle(ServerSocket server) {
        this.server = server;
        this.clients = new ArrayList<TalkerHandle>();
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

        while (!Thread.currentThread().isInterrupted()) {
            try {
                client = server.accept();

                System.out.print("New client @(");
                System.out.print(client.getInetAddress().getHostAddress());
                System.out.print(':');
                System.out.print(client.getPort());
                System.out.println(").");

                clients.add(new TalkerHandle(client));
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }
}
