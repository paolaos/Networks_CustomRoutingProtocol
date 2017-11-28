import java.util.ArrayDeque;
import java.util.Scanner;

public class MainInterface {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Favor indicar el puerto utilizado para que los demás nodos de su misma red se puedan comunicar con usted.");
        int localNetworkPort = scanner.nextInt();
        System.out.printf("Ahora indique cuál es su dirección IP (virtual)");
        String ipVirtualAddress = scanner.next();
        System.out.println("Cuál es su MAC address virtual?");
        String macAddress = scanner.next();
        System.out.println("Finalmente, cómo identifica usted a su compañero de router?");
        String routerMacAddress = scanner.next();
        System.out.println("Listo!");

        ArrayDeque<Envelope> inbox = new ArrayDeque<>();
        Thread thread = new Interface(localNetworkPort, inbox);
        thread.start();
        Interface interfac = new Interface(ipVirtualAddress, macAddress, inbox, routerMacAddress);


        System.out.println("Presione 1 para mandar un mensaje, o bien presione 0 para anticipar un mensaje");
        int response = scanner.nextInt();
        if(response == 1) {
            System.out.println("Escriba la dirección IP virtual de la persona a la que le quiere mandar un mensaje");
            String ipReceiver = scanner.next();
            System.out.println("Diga el número de acción de su mensaje ");
            response = scanner.nextInt();
            String actionIp = "";
            if(response == 1 || response == 2) {
                System.out.println("Escriba la dirección IP virtual de la acción de mensaje");
                actionIp = scanner.next();

            }

            System.out.println("Finalmente, escriba el cuerpo del mensaje, alfanumérico y sin sobrepasarse de los 1200 caracteres");
            String body = scanner.next();
            interfac.sendMessage(ipReceiver, response, actionIp, body);

        }
        interfac.wakeUp();

        /*int[] senderIp = {123,45,67,7};
        int[] receiverIp = {123,45,67,8};
        int[] actionIp = {};
        Message message = new Message(senderIp, receiverIp, 3, actionIp, "Hola!");
        Envelope envelope = new ExternalEnvelope("Legos1", "Bolinchas.Kevin", message);
        inbox.add(envelope);*/

    }
}
