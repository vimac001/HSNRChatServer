package HSNRChat.Server.Networking;

import HSNRChat.Server.Networking.Database.*;
import HSNRChat.Server.Networking.Networking.ChatServer;

import java.io.*;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {

    private static void initTransitions(String filePath) throws IOException {
        File fl = new File(filePath);

        if(fl.canRead() && fl.length() > 0) {
            FileReader reader = new FileReader(fl);
            BufferedReader br = new BufferedReader(reader);

            Stream<String> lns = br.lines();
            lns.forEach(new Consumer<String>() {
                public void accept(String element) {
                    String[] cls = element.split(",");
                    try {
                        Transition.create(cls[1], Byte.parseByte(cls[0]), Byte.parseByte(cls[2]), TransitionKind.fromString(cls[3]));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            br.close();
            reader.close();
        }
    }

    private static void initRooms(String filePath) throws IOException {
        File fl = new File(filePath);

        if(fl.canRead() && fl.length() > 0) {
            FileReader reader = new FileReader(fl);
            BufferedReader br = new BufferedReader(reader);

            Stream<String> lns = br.lines();
            lns.forEach(new Consumer<String>() {
                public void accept(String element) {
                    try {
                        Room.create(element);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            br.close();
            reader.close();
        }
    }


    public static void main(String[] args) throws IOException {
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


/*
        Room[] rooms;
        Transition[] transitions;

        try {
            rooms = Room.loadAll();
            transitions = Transition.loadAll();

            if(rooms.length <= 0) {
                try {
                    initRooms("./src/HSNRChat/Server/Networking/Database/Ressources/rooms.csv");
                    rooms = Room.loadAll();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if(transitions.length <= 0) {
                try {
                    initTransitions("./src/HSNRChat/Server/Networking/Database/Ressources/transitions.csv");
                    transitions = Transition.loadAll();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

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
        */