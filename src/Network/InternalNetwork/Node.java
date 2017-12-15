package Network.InternalNetwork;

import Network.Envelope.Envelope;
import Network.Envelope.InternalEnvelope;
import Network.Interface;
import Network.Message.Message;
import Network.Toolbox.Toolbox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Main abstract interface for the internal network. The biggest difference is the new inbox queue in charge of storing
 * all messages to be processed.
 */
public abstract class Node extends Interface {
    Queue<Envelope> inbox;

    public Node (String threadName, ArrayDeque<Envelope> inbox, Map<String, String> addressLocator, Map<String, String> ipTable){
        super(threadName, addressLocator, ipTable);
        this.inbox = inbox;
    }

    public void run(){
        if(getName().equals("serverActivation")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Favor indicar el puerto utilizado para recibir mensajes.");
            this.realReceivingPort = scanner.next();
            try {
                serverSocket = new ServerSocket(Integer.parseInt(this.realReceivingPort));
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Ya me conecté. ");
                    DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                    String inputLine = dataIn.readUTF();
                    System.out.println("Me llegó esto: " + inputLine);
                    String[] inputContent = inputLine.split(";");
                    Envelope envelope;
                    envelope = new InternalEnvelope();
                    envelope.setSender(inputContent[0]);
                    envelope.setReceiver(inputContent[1]);
                    envelope.setMessage(toolbox.convertStringToMessage(inputContent[2]));
                    this.addToInbox(envelope);
                    clientSocket.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if(getName().equals("ordinarySection")) {
                this.prepare();
                this.wakeUp();
            }
        }
    }

    synchronized void checkMessages(){
        if(!inbox.isEmpty()) {
            this.processMessage(inbox.poll().getMessage());

        }

    }

    void wakeUp(){
        System.out.println("Waking up...");
        try {
            while(true) {
                this.checkMessages();
                sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    synchronized void addToInbox(Envelope envelope) {
        inbox.add(envelope);
    }



}
