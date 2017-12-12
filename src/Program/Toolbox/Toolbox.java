package Program.Toolbox;

import Program.Message.Message;


//todo modificaciones de StringBuilder, hacer pruebas!
public class Toolbox {

    public String convertMessageToString(Message message) {
        String result = "";
        StringBuilder tempSender = new StringBuilder();
        StringBuilder tempReceiver = new StringBuilder();
        for(int i : message.getSenderIp()) {
            tempSender.append(String.valueOf(i)).append(".");

        }
        tempSender = new StringBuilder(tempSender.substring(0, tempSender.length() - 1));
        result += this.countZeroesIp(tempSender.toString());

        for(int i : message.getReceiverIp()) {
            tempReceiver.append(String.valueOf(i)).append(".");
        }

        tempReceiver = new StringBuilder(tempReceiver.substring(0, tempReceiver.length() - 1));
        result += this.countZeroesIp(tempReceiver.toString());

        result += String.valueOf(message.getAction());

        StringBuilder tempActionIp = new StringBuilder("000");

        if(message.getAction() <= 2 && message.getAction() >= 1) {
            for (int i : message.getActionIp()) {
                tempActionIp.append(String.valueOf(i)).append(".");

            }

            tempActionIp = new StringBuilder(tempActionIp.substring(0, tempActionIp.length() - 1));

        }

        result += tempActionIp;
        result += message.getMessage();
        return result;

    }

    public Message convertStringToMessage(String stringMessage) {
        String senderIpString = stringMessage.substring(0, 12);
        String receiverIpString = stringMessage.substring(12, 24);
        int[] senderIp = new int[4];
        int[] receiverIp = new int[4];
        int offset = 0;
        for(int i = 0; i < senderIp.length; i++) {
            senderIp[i] = Integer.parseInt(senderIpString.substring(offset, offset+3));
            receiverIp[i] = Integer.parseInt(receiverIpString.substring(offset, offset+3));
            offset = offset + 3;

        }

        int action = Integer.parseInt(stringMessage.substring(24,25));
        int[] actionIp = new int[4];
        String message;

        if(action <= 2 && action != 0) {
            offset = 0;
            String actionIpString = stringMessage.substring(25, 37);
            for(int i = 0 ; i < 4; i++) {
                actionIp[i] = Integer.parseInt(actionIpString.substring(offset, offset+3));
                offset = offset + 3;

            }
            message = stringMessage.substring(37, stringMessage.length());

        } else
            message = stringMessage.substring(28, stringMessage.length());

        return new Message(senderIp, receiverIp, action, actionIp, message);

    }

    public String countZeroesIp(String str) {
        StringBuilder result = new StringBuilder();
        String[] numbers = str.split("\\.");
        for(String s: numbers) {
            if(s.length() < 3) {
                int zeroes = 3 - s.length();
                StringBuilder sBuilder = new StringBuilder(s);
                while(zeroes > 0) {
                    sBuilder.insert(0, "0");
                    zeroes--;
                }
                s = sBuilder.toString();
            }
            result.append(s);
        }
        return result.toString();
    }

    public String convertIpToString(int[] messageIp){
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < 4; i++)
            result.append(String.valueOf(messageIp[i])).append(".");

        return result.substring(0, result.length() - 1);

    }

}
