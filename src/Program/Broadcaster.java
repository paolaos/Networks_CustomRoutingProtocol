package Program;

import Program.Envelope.Envelope;
import Program.Message.Message;
import Program.Toolbox.Toolbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Broadcaster extends Node {

    public Broadcaster(String threadName, ArrayDeque<Envelope> inbox) {
        super(threadName, inbox);
        this.inbox = inbox;
        this.toolbox = new Toolbox();
        this.addressLocator = new TreeMap<>();
        this.ipTable = new TreeMap<>();

    }

    protected void prepare(){
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Indique cuál es su dirección IP (virtual)");
        this.virtualIpAddress = scanner.next();
        System.out.println("Cuál es su dirección IP (real)?");
        this.realIpAddress = scanner.next();
        this.macAddress = "dispatcher";
        System.out.println("Cuál es su puerto para mandar mensajes?");
        this.realSendingPort = scanner.next();

    }

    protected void processMessage(Message message){
        switch (message.getAction()) {
            case 0: //entra macAddress realIpAddress;realSendingPort
                String[] splitString = message.getMessage().split(" ");
                this.addressLocator.put(splitString[0], splitString[1]);
                System.out.println("Entró: " + splitString[0] + ", " + splitString[1]);
                break;
            case 1: //BROADCAST
                for(Map.Entry<String, String> entry : addressLocator.entrySet()) {
                    String[] realInformation = entry.getValue().split(";");
                    this.send(entry.getKey(), message.getMessage(), realInformation[0], Integer.parseInt(realInformation[1]));
                }
                break;
        }


    }


}
