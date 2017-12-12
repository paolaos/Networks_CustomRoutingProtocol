package Program.Message;

public class Message {
    private int[] senderIp;
    private int[] receiverIp;
    private int action;
    private int[] actionIp;
    private String message;

    public Message(int[] senderIp, int[] receiverIp, int action, int[] actionIp, String message) {
        this.senderIp = senderIp;
        this.receiverIp = receiverIp;
        this.action = action;
        this.actionIp = actionIp;
        this.message = message;

    }


    public int[] getSenderIp() {
        return senderIp;
    }

    public int[] getReceiverIp() {
        return receiverIp;
    }

    public int getAction() {
        return action;
    }

    public int[] getActionIp() {
        return actionIp;
    }

    public String getMessage() {
        return message;
    }
}
