package HSNRChat.Server;

import java.io.*;
import java.net.Socket;

public class TalkerHandle {

    private  Socket client;

    private InputStream in;
    private OutputStream out;

    private PrintWriter writer;
    private BufferedReader reader;

    private Thread tListener;

    public TalkerHandle(Socket client) {
        this.client = client;

        if(client.isConnected()){
            try {
                in = client.getInputStream();
                out = client.getOutputStream();

                writer = new PrintWriter(out);
                reader = new BufferedReader(new InputStreamReader(in));

                tListener = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!Thread.currentThread().isInterrupted()) {
                            String msg = null;
                            try {
                                msg = reader.readLine();
                                if(msg == null) {
                                    System.out.println("Client closed connection!");
                                    Thread.currentThread().interrupt();
                                }else{
                                    System.out.println(" >> " + msg);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                tListener.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        writer.print(msg);
        writer.flush();
    }
}
