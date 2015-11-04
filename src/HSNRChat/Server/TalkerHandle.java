package HSNRChat.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TalkerHandle {

    private  Socket client;

    private InputStream in;
    private OutputStream out;

    private PrintWriter writer;

    public TalkerHandle(Socket client) {
        this.client = client;

        if(client.isConnected()){
            try {
                in = client.getInputStream();
                out = client.getOutputStream();

                writer = new PrintWriter(out);
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
