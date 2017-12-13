import Program.Envelope.Envelope;
import Program.Interface;
import Program.Broadcaster;
import Program.Node;

import java.util.ArrayDeque;
import java.util.Scanner;

public class Controller {
    public synchronized void run() {
        ArrayDeque<Envelope> inbox = new ArrayDeque<>();
        Scanner scanner = new Scanner(System.in);
        Thread serverThread;
        Thread ordinaryThread;
        System.out.println("Digite 0 si usted es un nodo de Interfaz, o 1 si es un nodo tipo Broadcast. ");
        int isInterface = scanner.nextInt();

        if(isInterface == 0) {
            serverThread = new Interface("serverActivation", inbox);
            serverThread.start();


            ordinaryThread = new Interface("ordinarySection", inbox);
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
