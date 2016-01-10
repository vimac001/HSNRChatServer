package HSNRChat.Server.Networking.Networking.Streaming;

import HSNRChat.Server.Networking.Networking.ClientFunction;
import HSNRChat.Server.Networking.Networking.ServerFunction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StructuredOutputStream extends DataOutputStream {
    /**
     * Constructs a new {@code StructuredOutputStream} on the {@code OutputStream}
     * {@code out}. Note that data written by this stream is not in a human
     * readable form but can be reconstructed by using a {@link StructuredOutputStream}
     * on the resulting output.
     *
     * @param out the target stream for writing.
     */
    public StructuredOutputStream(OutputStream out) {
        super(out);
    }
    
    public void writeFunction(ServerFunction fnc) throws IOException {
        this.writeByte(fnc.getId());
    }

    public void writeFunction(ClientFunction fnc) throws IOException {
        this.writeByte(fnc.getId());
    }
}
