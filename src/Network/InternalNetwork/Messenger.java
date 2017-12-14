package Network.InternalNetwork;

import Network.Envelope.*;
import Network.Message.*;
import Network.Toolbox.*;

import java.util.*;

public class Messenger extends Node {
    public Messenger(String threadName, ArrayDeque<Envelope> inbox){
        super(threadName, inbox);
        this.inbox = inbox;
        this.toolbox = new Toolbox();
        this.addressLocator = new TreeMap<>();
        this.ipTable = new TreeMap<>();

    }

    protected void prepare() {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Indique cuál es su dirección IP (virtual)");
        this.virtualIpAddress = scanner.next();
        System.out.println("Cuál es su dirección IP (real)?");
        this.realIpAddress = scanner.next();
        System.out.println("Cuál es su MAC address virtual?");
        this.macAddress = scanner.next();
        System.out.println("Cuál es su puerto para mandar mensajes?");
        this.realSendingPort = scanner.next();
        System.out.println("Cuál es su puerto para recibir mensajes?");
        this.realReceivingPort = scanner.next();


        System.out.println("Cuál es la dirección virtual de su broadcaster?");
        String virtualDispatcherIpAddress = scanner.next();
        System.out.println("Cuál es la dirección IP de su broadcaster?");
        String realDispatcherIpAddress = scanner.next();
        System.out.println("Cuál es el puerto de su broadcaster?");
        String broadcasterPort = scanner.next();
        this.ipTable.put(virtualDispatcherIpAddress, "broadcaster");
        this.addressLocator.put("broadcaster", realDispatcherIpAddress + "," + broadcasterPort);
        String stringMessage = this.createMessage(virtualDispatcherIpAddress, 0, "", this.macAddress + " " + this.realIpAddress + "," + this.realReceivingPort);
        this.send("broadcaster", stringMessage, realDispatcherIpAddress, Integer.parseInt(broadcasterPort));

        System.out.println("Ahora hay que agregar a sus vecinos router o interfaces router. Presione 1 para agregar, o 0 para omitir.");
        int result = scanner.nextInt();

        if(result == 1) {
            while (result == 1) {
                System.out.println("Presione 0 si es un vecino router, o 1 si es una interfaz router");
                result = scanner.nextInt();

                System.out.println("Digite la dirección IP virtual de su compañero de router");
                String routerVirtualIpAddress = scanner.next();
                System.out.println("Digite el nombre por el cual usted conoce al router");
                String routerMacAddress = scanner.next();
                this.ipTable.put(routerVirtualIpAddress, routerMacAddress);
                System.out.println("Digite la dirección IP real de su compañero de router");
                String routerRealIpAddress = scanner.next();
                System.out.println("También digite su número de puerto");
                String routerPort = scanner.next();
                this.addressLocator.put(routerMacAddress, routerRealIpAddress + "," + routerPort);
                if(result == 1) {
                    stringMessage = this.createMessage(routerVirtualIpAddress, 2, "", this.macAddress + " " + this.realIpAddress + "," + this.realReceivingPort);
                    this.send(routerMacAddress, stringMessage, routerRealIpAddress, Integer.parseInt(routerPort));

                }

                System.out.println("Digite 1 para agregar a otro router, o bien 0 para terminar de agregar mas vecinos router");
                result = scanner.nextInt();

            }

        }

        System.out.println("Ahora tiene que agregar a las redes inalcanzables por esta red cuyo camino para " +
                "llegar a ellas lo incluye a uno de sus compañeros de router. Presione 1 para agregar, o 0 para omitir.");
        result = scanner.nextInt();

        if(result == 1) {
            while (result == 1) {
                System.out.println("Digite la dirección IP virtual de donde se querría ir. ");
                String address = scanner.next();
                System.out.println("Digite a través de cuál router (nombre) se tendría que ir. ");
                String router = scanner.next();
                this.ipTable.put(address, router);
                System.out.println("Digite 1 para agregar a otra dirección, o bien 0 para terminar de agregar mas vecinos router");
                result = scanner.nextInt();

            }
        }


        System.out.println("Presione 1 para mandar un mensajes, o bien presione 0 para omitir");
        int response = scanner.nextInt();
        if(response == 1) {
            while(response == 1) {
                System.out.println("Escriba la dirección IP virtual de la persona a la que le quiere mandar un mensaje");
                String ipReceiver = scanner.next();
                System.out.println("Diga el número de acción de su mensaje ");
                response = scanner.nextInt();
                String actionIp = "";
                if (response == 1) {
                    System.out.println("Escriba la dirección IP virtual de la acción de mensaje");
                    actionIp = scanner.next();

                }

                System.out.println("Finalmente, escriba el cuerpo del mensaje, alfanumérico y sin sobrepasarse de los 1200 caracteres");
                String body = scanner.next();
                this.createMessage(ipReceiver, response, actionIp, body);
                System.out.println("Escriba 1 para mandar otro mensaje, o 0 para terminar de mandar mensajes");
                response = scanner.nextInt();

            }
        }

    }

    protected synchronized void processMessage(Message message){
        String messageReceiverIp = toolbox.convertIpToString(message.getReceiverIp());
        String messageSenderIp = toolbox.convertIpToString(message.getSenderIp());

        switch(message.getAction()) {
            case 0: //ninguna acción solicitada
                if(messageReceiverIp.equals(this.virtualIpAddress))
                    System.out.println("Message from " + messageSenderIp + ": " + message.getMessage());

                else {
                    if(this.ipTable.get(messageReceiverIp) == (null)) { //revisar
                        this.ipTable.put(messageReceiverIp, "idk");
                        this.addToInbox(new InternalEnvelope(macAddress, macAddress, message));

                        String bodyMessage = this.createMessage(this.virtualIpAddress, 1, messageReceiverIp, "");
                        String[] realInformation = this.addressLocator.get("dispatcher").split(",");
                        this.send("dispatcher", bodyMessage, realInformation[0], Integer.parseInt(realInformation[1]));

                    } else {
                        if(this.ipTable.get(messageReceiverIp).equals("idk"))
                            this.addToInbox(new InternalEnvelope(macAddress, macAddress, message));

                        else {
                            String closestMacAddress = this.ipTable.get(messageReceiverIp);
                            String realInformation = addressLocator.get(closestMacAddress);
                            if(realInformation == null)
                                System.err.println("Error: no hay dirección IP real de una dirección MAC ya registrada");

                            else {
                                String[] receiverRealInformation = realInformation.split(",");
                                this.send(closestMacAddress, toolbox.convertMessageToString(message), receiverRealInformation[0], Integer.parseInt(receiverRealInformation[1]));

                            }
                        }
                    }
                }

                break;

            case 1: //conoce esta dirección IP? BODY(del que pregunta): macAddress realIpAddress;realSendingPort
                String ipAddress = toolbox.convertIpToString(message.getActionIp());
                int actionNumber = -1;
                String body = "";
                if(ipAddress.equals(this.virtualIpAddress)) {
                    actionNumber = 2;
                    body = this.macAddress + " " + this.realIpAddress + "," + this.realSendingPort;
                } else {
                    if(this.ipTable.containsKey(ipAddress)) {
                        body = toolbox.convertIpToString(message.getActionIp()) + " " + this.macAddress + " " + this.realIpAddress + "," + this.realSendingPort;
                        actionNumber = 3;
                    }
                }
                if(actionNumber > -1) {
                    String[] senderInformation = message.getMessage().split(" ");
                    String[] realInformation = senderInformation[1].split(",");
                    String bodyMessage = this.createMessage(messageSenderIp, actionNumber, "", body);
                    this.send(senderInformation[0], bodyMessage, realInformation[0], Integer.parseInt(realInformation[1]));
                }

                break;

            case 2: //yep, soy yo. BODY: macAddress realIpAddress;realSendingPort
                String[] splitString = message.getMessage().split(" ");
                this.ipTable.put(messageSenderIp, splitString[0]);
                this.addressLocator.put(splitString[0], splitString[1]);
                break;

            case 3: //yep, yo conozco esa dirección IP, es através mío. BODY: ipVirtualAddressSolicitado macAddress realIpAddress;realSendingPort
                splitString = message.getMessage().split(" ");
                this.ipTable.put(splitString[0], splitString[1]);
                this.addressLocator.put(splitString[1], splitString[2]);
                break;

        }


    }

    private String createMessage(String ipReceiver, int actionNumber, String ipAction, String messageBody) {
        String result = "";
        result += toolbox.countZeroesIp(this.virtualIpAddress);
        result += toolbox.countZeroesIp(ipReceiver);
        result += String.valueOf(actionNumber);
        result += toolbox.countZeroesIp(ipAction);
        result += messageBody;

        return result;

    }

}
