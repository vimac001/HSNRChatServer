package HSNRChat.Server.Networking;

import HSNRChat.Server.Networking.Database.Room;
import HSNRChat.Server.Networking.Database.Transition;
import HSNRChat.Server.Networking.Networking.ChatServer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        Room[] rooms;
        Transition[] transitions;

        try {
            rooms = Room.loadAll();
            transitions = Transition.loadAll();

            for(Room rm : rooms) {
                System.out.print(rm.getId());
                System.out.print("\t:\t");
                System.out.println(rm.getName());
            }

            for(Transition tr : transitions) {
                System.out.print(tr.getId());
                System.out.print("\t:\t");
                System.out.print(tr.getName());
                System.out.print("\t:\t");
                System.out.print(tr.getArea());
                System.out.print("\t:\t");
                System.out.print(tr.getSemester());
                System.out.print("\t:\t");
                System.out.println(tr.getKind().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        /*

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

        */
    }
}
