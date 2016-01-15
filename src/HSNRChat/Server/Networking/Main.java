package HSNRChat.Server.Networking;

import HSNRChat.Server.Networking.Database.DataManager;
import HSNRChat.Server.Networking.Database.Room;
import HSNRChat.Server.Networking.Database.Transition;
import HSNRChat.Server.Networking.Database.TransitionKind;
import HSNRChat.Server.Networking.Networking.ChatServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 7;

    public static void main(String[] args) throws IOException {
        System.out.println("HSNR Chat Server " + MAJOR_VERSION + '.' + MINOR_VERSION);
        try {
            DataManager.connect();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}