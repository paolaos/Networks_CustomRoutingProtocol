package Network.Envelope;

import Network.Message.Message;

public class InternalEnvelope extends Envelope {

    public InternalEnvelope(){

    }

    public InternalEnvelope(String ipSender, String ipReceiver, Message message){
        this.sender = ipSender;
        this.receiver = ipReceiver;
        this.message = message;
    }
    //action;macsender;macreceiver;message(entero);

}
