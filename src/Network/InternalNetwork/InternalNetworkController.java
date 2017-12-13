package Network.InternalNetwork;

import Network.Envelope.Envelope;

import java.util.ArrayDeque;
import java.util.Scanner;

public class InternalNetworkController {
    public synchronized void run() {
        ArrayDeque<Envelope> inbox = new ArrayDeque<>();
        Scanner scanner = new Scanner(System.in);
        Thread serverThread;
        Thread ordinaryThread;
        System.out.println("Digite 0 si usted es un nodo de Interfaz, o 1 si es un nodo tipo Broadcast. ");
        int isInterface = scanner.nextInt();

        if(isInterface == 0) {
            serverThread = new Messenger("serverActivation", inbox);
            serverThread.start();


            ordinaryThread = new Messenger("ordinarySection", inbox);
            try {
                ordinaryThread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ordinaryThread.start();
        } else {
            serverThread = new Broadcaster("serverActivation", inbox);
            serverThread.start();

            ordinaryThread = new Broadcaster("ordinarySection", inbox);
            try {
                ordinaryThread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ordinaryThread.start();
        }
    }
}
