package Network.InternalNetwork;

import Network.Envelope.Envelope;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Controller class of the whole InternalNetwork package, called by the Controller method
 */
public class InternalNetworkController {
    public synchronized void run() {
        ArrayDeque<Envelope> inbox = new ArrayDeque<>();
        Map<String, String> ipTable = new TreeMap<>();
        Map<String, String> addressLocator = new TreeMap<>();
        Scanner scanner = new Scanner(System.in);
        Thread serverThread;
        Thread ordinaryThread;
        System.out.println("Digite 0 si usted es un nodo de Interfaz, o 1 si es un nodo tipo Broadcast. ");
        int isInterface = scanner.nextInt();

        if(isInterface == 0) {
            serverThread = new Messenger("serverActivation", inbox, addressLocator, ipTable);
            serverThread.start();


            ordinaryThread = new Messenger("ordinarySection", inbox, addressLocator, ipTable);
            try {
                ordinaryThread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ordinaryThread.start();
        } else {
            serverThread = new Broadcaster("serverActivation", inbox, addressLocator, ipTable);
            serverThread.start();

            ordinaryThread = new Broadcaster("ordinarySection", inbox, addressLocator, ipTable);
            try {
                ordinaryThread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ordinaryThread.start();
        }
    }
}
