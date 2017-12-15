package Network.InternalNetwork;

import Network.Envelope.Envelope;
import Network.Message.Message;
import Network.Toolbox.Toolbox;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class is the implementation of the broadcaster interface, which is mainly in charge of storing location
 * information from other interfaces in the network.
 */
public class Broadcaster extends Node {


    public Broadcaster(String threadName, ArrayDeque<Envelope> inbox, Map<String, String> addressLocator, Map<String, String> ipTable) {
        super(threadName, inbox, addressLocator, ipTable);
    }

    protected void prepare(){
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Indique cuál es su dirección IP (virtual)");
        this.virtualIpAddress = scanner.next();
        System.out.println("Cuál es su dirección IP (real)?");
        this.realIpAddress = scanner.next();
        this.macAddress = "broadcaster";
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
                    String[] realInformation = entry.getValue().split(",");
                    this.send(entry.getKey(), toolbox.convertMessageToString(message), realInformation[0], Integer.parseInt(realInformation[1]));
                }
                break;
        }


    }


}
