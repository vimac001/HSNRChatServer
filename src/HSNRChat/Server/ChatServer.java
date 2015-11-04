
package HSNRChat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Scanner;

public class ChatServer {

    public static final int Port = 1337;

    private static ServerSocket server;
    private static Thread tListener;
    private static ServerHandle listener;

    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        try {
            server = new ServerSocket(Port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(server.isBound()) {
            System.out.print("Server started @(");
            System.out.print(Port);
            System.out.println(").");

            listener = new ServerHandle(server);
            tListener = new Thread(listener);
            tListener.run();
        }

        try {
            String line = null;
            do {
                if(line != null && line != "exit") {
                    listener.broadcast(line);
                    System.out.println(" << " + line);
                }

                System.out.print("Message: ");
                line = br.readLine();
            }while(line != "exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
