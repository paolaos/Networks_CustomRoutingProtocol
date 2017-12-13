package Network.ExternalNetwork;

import Network.Envelope.Envelope;

import java.util.Date;

public class BufferNode {
    //algoritmo, clock
    private Envelope envelope;
    private BufferNodeState state;
    private Date timestamp;
    private int id;


    public void setId(int id) {
        this.id = id;
    }

    public void setState(BufferNodeState state) {
        this.state = state;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public BufferNodeState getState() {
        return state;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }
}
