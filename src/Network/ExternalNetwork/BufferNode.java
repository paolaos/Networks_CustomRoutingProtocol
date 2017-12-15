package Network.ExternalNetwork;

import Network.Envelope.Envelope;

import java.util.Date;

/**
 * All basic components of each node from the buffer.
 */
public class BufferNode {
    //replacement algorithm: clock
    private Envelope envelope;
    private BufferNodeState state;
    private Date timestamp;
    private int id;


    void setId(int id) {
        this.id = id;
    }

    void setState(BufferNodeState state) {
        this.state = state;
    }

    void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    Envelope getEnvelope() {
        return envelope;
    }

    BufferNodeState getState() {
        return state;
    }

    Date getTimestamp() {
        return timestamp;
    }

    int getId() {
        return id;
    }
}
