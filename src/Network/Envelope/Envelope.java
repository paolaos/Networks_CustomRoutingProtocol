package Network.Envelope;
import Network.Message.Message;

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
