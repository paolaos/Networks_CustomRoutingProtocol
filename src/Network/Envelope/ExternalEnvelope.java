package Network.Envelope;
import Network.Message.Message;

/**
 * Envelope format that was previously built with bolinchas team.
 */
public class ExternalEnvelope extends Envelope {
    public ExternalEnvelope(){

    }
    public ExternalEnvelope(String macSender, String macReceiver, Message messageBody){
        this.sender = macSender;
        this.receiver = macReceiver;
        this.message = messageBody;
    }
    //share;ipsender;ipreceiver;message(body)
}
