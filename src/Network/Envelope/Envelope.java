package Network.Envelope;
import Network.Message.Message;

/**
 * Abstract template of what all basic envelopes should contain. Envelopes are the simulation of a physical layer in
 * the network.
 */
public abstract class Envelope {
    String sender;
    String receiver;
    Message message;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
