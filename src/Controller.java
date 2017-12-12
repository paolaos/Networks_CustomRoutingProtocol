import Program.Envelope.Envelope;
import Program.Interface;
import Program.Broadcaster;
import Program.Node;

import java.util.ArrayDeque;
import java.util.Scanner;

public class Controller {
    private ArrayDeque<Envelope> inbox;
    public void run() {
        Node node;
        inbox = new ArrayDeque<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Favor indicar el puerto utilizado para recibir mensajes.");
        int localNetworkPort = scanner.nextInt();
        Thread thread;
        System.out.println("Digite 0 si usted es un nodo de Interfaz, o 1 si es un nodo tipo Broadcast. ");
        boolean isInterface = scanner.nextBoolean();
        if(isInterface) {
            thread = new Interface(localNetworkPort, inbox);
            thread.start();

            node = new Interface(inbox);
            node.begin();
        } else {
            thread = new Broadcaster(localNetworkPort, inbox);
            thread.start();

            node = new Broadcaster(inbox);
            node.begin();
        }
    }
}
