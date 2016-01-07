package HSNRChat.Server.Networking.Networking;

import HSNRChat.Server.Networking.Networking.Streaming.StructuredInputStream;

import java.io.IOException;

public class ClientListener implements Runnable {

    private StructuredInputStream is;
    private ClientHandle handle;

    public ClientListener(StructuredInputStream is, ClientHandle handle) {
        this.is = is;
        this.handle = handle;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted() && this.handle.isConnected()) {
            try {
                ServerFunction fnc = this.is.readFunction();
                if(fnc != ServerFunction.Undefined) {
                    this.handle.onNewCall(fnc);
                } else {
                    //TODO: What to do on wrong server function. (Ignore!??!)
                }
            } catch (IOException e) {
                this.handle.onClosed();
            }
        }
    }
}
