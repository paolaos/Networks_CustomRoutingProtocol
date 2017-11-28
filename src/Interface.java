import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Interface extends Thread {
    private String ipVirtualAddress;
    private String macAddress;
    private String routerMacAddress;

    private Dispatcher dispatcher;
    public Queue<Envelope> inbox;
    private Map<String, String> cache;

    private ServerSocket serverSocket;
    private Socket clientSocket;


    public Interface(String ipVirtualAddress, String macAddress, ArrayDeque<Envelope> inbox, String routerMacAddress){
        this.macAddress = macAddress;
        this.ipVirtualAddress = ipVirtualAddress;
        this.routerMacAddress = routerMacAddress;

        dispatcher = new Dispatcher();
        this.inbox = inbox;
        cache = new TreeMap<>();



    }

    public Interface(int localNetworkPort, ArrayDeque<Envelope> inbox) {
        try {
            this.inbox = inbox;
            serverSocket = new ServerSocket(localNetworkPort);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wakeUp(){
        System.out.println("Waking up...");
        while(true)
            this.checkMessages();

    }

    private synchronized void checkMessages(){
        if(!inbox.isEmpty()) {
            this.processMessage(inbox.poll().getMessage());

        }

    }

    private void processMessage(Message message){
        String messageReceiverIp = "";
        int [] messageReceiverIpArray = message.getReceiverIp();
        String messageSenderIp = "";
        int [] messageSenderIpArray = message.getSenderIp();

        for(int i = 0; i < message.getReceiverIp().length; i++) {
            messageReceiverIp += String.valueOf(messageReceiverIpArray[i]) + ".";
            messageSenderIp += String.valueOf(messageSenderIpArray[i] + ".");

        }
        messageReceiverIp = messageReceiverIp.substring(0, messageReceiverIp.length() - 1);
        messageSenderIp = messageSenderIp.substring(0, messageSenderIp.length() - 1);

        if(messageReceiverIp.equals(this.ipVirtualAddress))
            System.out.println("Message from " + messageSenderIp + ": " + message.getMessage());

        else {
            String closestMacAddress = checkIpTable(messageReceiverIp);
            String newReceiverRealIpAddressAndPort = cache.get(closestMacAddress);
            if(newReceiverRealIpAddressAndPort == null) {
                newReceiverRealIpAddressAndPort = this.dispatcher.fetch(closestMacAddress);
                cache.put(closestMacAddress, newReceiverRealIpAddressAndPort);

            }

            String[] receiverRealInformation = newReceiverRealIpAddressAndPort.split(";");

            String beginningOfMyMacAddress = this.macAddress.substring(0, 4);
            String beginningClosestMacAddress = closestMacAddress.substring(0,4);

            if(beginningOfMyMacAddress.equals(beginningClosestMacAddress))
                this.send("3", macAddress, closestMacAddress, this.assembleStringMessage(message), "; ",receiverRealInformation[0], Integer.parseInt(receiverRealInformation[1]));

            else
                this.send("share", messageSenderIp, messageReceiverIp, message.getMessage(), "", receiverRealInformation[0], Integer.parseInt(receiverRealInformation[1]));

        }

    }

    private void send(String header, String sender, String receiver, String body, String footer, String realIpAddress, int realPort){
        String message = header + ";" + sender + ";" + receiver + ";" + body + footer;

        try {
            Socket socket = new Socket(realIpAddress, realPort);
            System.out.println("Conectada a servidor.");
            OutputStream os = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            out.writeUTF(message);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String checkIpTable(String ipAddress){
        File file = new File("ipTable.txt");
        String result = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            int shortestRoute = Integer.MAX_VALUE;
            String line = "";
            while((line = br.readLine()) != null) {
                String key = line.split(" ")[0];
                if(key.equals(ipAddress) && Integer.parseInt(line.split(" ")[1])< shortestRoute) {
                    shortestRoute = Integer.parseInt(line.split(" ")[1]);
                    result = line.split(" ")[2];

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }



    public String assembleStringMessage(Message message) {
        String result = "";
        for(int i : message.getSenderIp()) {
            String temp = String.valueOf(i);
            if(temp.length() < 3) {
                int zeroes = 3 - temp.length();
                while(zeroes > 0) {
                    temp = "0" + temp;
                    zeroes--;
                }
            }
            result += temp;

        }

        for(int i : message.getReceiverIp()) {
            String temp = String.valueOf(i);
            if(temp.length() < 3) {
                int zeroes = 3 - temp.length();
                while(zeroes > 0) {
                    temp = "0" + temp;
                    zeroes--;
                }
            }
            result += temp;
        }

        result += String.valueOf(message.getAction());

        if(message.getAction() <= 2 && message.getAction() >= 1) {
            for(int i : message.getActionIp()) {
                String temp = String.valueOf(i);
                if(temp.length() < 3) {
                    int zeroes = 3 - temp.length();
                    while(zeroes > 0) {
                        temp = "0" + temp;
                        zeroes--;
                    }
                }
                result += temp;
            }

        }

        result += message.getMessage();
        return result;

    }

    private Message disassembleStringMessage(String stringMessage) {
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
            message = stringMessage.substring(37, stringMessage.length() - 1);

        } else
            message = stringMessage.substring(25, stringMessage.length() - 1);

        return new Message(senderIp, receiverIp, action, actionIp, message);

    }


    public void run(){

        try {
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Ya me conecté. ");
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                String inputLine = dataIn.readUTF();
                System.out.println("Me llegó esto: " + inputLine);
                String[] inputContent = inputLine.split(";");
                Envelope envelope;
                if(inputContent[0].equals("share")) {
                    String[] numbers = inputContent[1].split("\\.");
                    String temp = "";
                    for(String s: numbers) {
                        if(s.length() < 3) {
                            int zeroes = 3 - s.length() + 0;
                            while(zeroes > 0) {
                                s = "0" + s;
                                zeroes--;
                            }
                        }
                        temp += s;
                    }

                    numbers = inputContent[2].split("\\.");
                    for(String s: numbers) {
                        if(s.length() < 3) {
                            int zeroes = 3 - s.length();
                            while(zeroes > 0) {
                                s = "0" + s;
                                zeroes--;
                            }
                        }
                        temp += s;
                    }
                    envelope = new ExternalEnvelope();
                    envelope.setSender(routerMacAddress);
                    envelope.setReceiver(macAddress);
                    String message = temp + "3" + inputContent[3];

                    envelope.setMessage(this.disassembleStringMessage(message));

                } else {
                    envelope = new InternalEnvelope();
                    envelope.setSender(inputContent[1]);
                    envelope.setReceiver(inputContent[2]);
                    envelope.setMessage(this.disassembleStringMessage(inputContent[3]));

                }

                this.inbox.add(envelope);

                clientSocket.close();

            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        //conexion que espera que le llegue mensaje.
    }


}
