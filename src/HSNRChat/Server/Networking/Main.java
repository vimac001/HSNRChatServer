package HSNRChat.Server.Networking;

import HSNRChat.Server.Networking.Networking.ChatServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ChatServer svr = new ChatServer();

        svr.run();

        byte[] buffer = new byte[1024];
        do {

            try {
                System.in.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(svr.isRunning());
    }
}
