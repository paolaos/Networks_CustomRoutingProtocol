package Program;

import Program.Envelope.Envelope;
import Program.Envelope.InternalEnvelope;
import Program.Message.Message;
import Program.Toolbox.Toolbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;

public abstract class Node extends Thread {
    String virtualIpAddress;
    String realIpAddress;
    String realSendingPort;
    String realReceivingPort;
    String macAddress;
    Map<String, String> addressLocator;
    ServerSocket serverSocket;
    Toolbox toolbox;
    Queue<Envelope> inbox;
    Map<String, String> ipTable;


    public void run(){
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ya me conecté. ");
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                String inputLine = dataIn.readUTF();
                System.out.println("Me llegó esto: " + inputLine);
                String[] inputContent = inputLine.split(";");
                Envelope envelope;
                envelope = new InternalEnvelope();
                envelope.setSender(inputContent[1]);
                envelope.setReceiver(inputContent[2]);
                envelope.setMessage(toolbox.convertStringToMessage(inputContent[3]));
                this.inbox.add(envelope);
                clientSocket.close();

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void checkMessages(){
        if(!inbox.isEmpty()) {
            this.processMessage(inbox.poll().getMessage());

        }

    }

    protected abstract void processMessage(Message message);

    protected abstract void prepare();

    public void begin() {
        this.prepare();
        this.wakeUp();
    }

    private void wakeUp(){
        System.out.println("Waking up...");
        while(true)
            this.checkMessages();

    }

    synchronized void send(String receiver, String body, String realIpAddress, int realPort) {
        String message = this.macAddress + ";" + receiver + ";" + body + ";";

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

    synchronized void addToInbox(Envelope envelope) {
        inbox.add(envelope);
    }
}
