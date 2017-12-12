package Program;

import Program.Envelope.*;
import Program.Message.*;
import Program.Toolbox.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Interface extends Node {

    public Interface(ArrayDeque<Envelope> inbox) {
        this.inbox = inbox;
        this.toolbox = new Toolbox();
        addressLocator = new TreeMap<>();

    }

    public Interface(int localNetworkPort, ArrayDeque<Envelope> inbox) {
        try {
            this.inbox = inbox;
            serverSocket = new ServerSocket(localNetworkPort);


        } catch (IOException e) {
            e.printStackTrace();
        }
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
        this.realPort = scanner.next();


        System.out.println("Cuál es la dirección virtual de su dispatcher?");
        String virtualDispatcherIpAddress = scanner.next();
        System.out.println("Cuál es la dirección IP de su dispatcher?");
        String realDispatcherIpAddress = scanner.next();
        System.out.println("Cuál es el puerto de su dispatcher?");
        String dispatcherPort = scanner.next();
        this.ipTable.put(virtualDispatcherIpAddress, "dispatcher");
        this.addressLocator.put(virtualDispatcherIpAddress, realDispatcherIpAddress + ";" + dispatcherPort);
        String stringMessage = this.createMessage(this.virtualIpAddress, 0, "", this.macAddress + " " + this.realIpAddress + ";" + this.realPort);
        Message message = this.toolbox.convertStringToMessage(stringMessage);
        this.processMessage(message);


        System.out.println("Digite 0 si usted es un nodo router, o 1 si usted es un nodo terminal.");
        int result = scanner.nextInt();
        if(result == 0) {
            System.out.println("Ahora hay que agregar a sus vecinos router. Presione 1 para agregar, o 0 para omitir.");
            result = scanner.nextInt();

            if(result == 1) {
                while (result == 1) {
                    System.out.println("Digite la dirección IP virtual de su compañero de router");
                    String address = scanner.next();
                    System.out.println("Digite el nombre por el cual usted conoce al router");
                    ipTable.put(address, scanner.next());
                    System.out.println("Digite 1 para agregar a otro router, o bien 0 para terminar de agregar mas vecinos router");
                    result = scanner.nextInt();

                }

            }

        } else {
            System.out.println("Presione 1 para mandar un mensajes, o bien presione 0 para omitir");
            int response = scanner.nextInt();
            if(response == 1) {
                while(response == 1) {
                    System.out.println("Escriba la dirección IP virtual de la persona a la que le quiere mandar un mensaje");
                    String ipReceiver = scanner.next();
                    System.out.println("Diga el número de acción de su mensaje ");
                    response = scanner.nextInt();
                    String actionIp = "";
                    if (response == 1 || response == 2) {
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

        System.out.println("Listo!");

    }

    protected synchronized void processMessage(Message message){
        String messageReceiverIp = toolbox.convertIpToString(message.getReceiverIp());
        String messageSenderIp = toolbox.convertIpToString(message.getSenderIp());

        switch(message.getAction()) {
            case 0: //ninguna acción solicitada
                if(messageReceiverIp.equals(this.virtualIpAddress))
                    System.out.println("Program.Message from " + messageSenderIp + ": " + message.getMessage());

                else {
                    if(this.ipTable.get(messageReceiverIp) == (null)) { //revisar
                        this.ipTable.put(messageReceiverIp, "idk");
                        this.addToInbox(new InternalEnvelope(macAddress, macAddress, message));

                        String bodyMessage = this.createMessage(this.virtualIpAddress, 1, messageReceiverIp, "");
                        String[] realInformation = this.addressLocator.get("dispatcher").split(";");
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
                                String[] receiverRealInformation = realInformation.split(";");
                                this.send(closestMacAddress, toolbox.convertMessageToString(message), receiverRealInformation[0], Integer.parseInt(receiverRealInformation[1]));

                            }
                        }
                    }
                }

                break;

            case 1: //conoce esta dirección IP? BODY(del que pregunta): macAddress realIpAddress;realPort
                String ipAddress = toolbox.convertIpToString(message.getActionIp());
                int actionNumber = -1;
                String body = "";
                if(ipAddress.equals(this.virtualIpAddress)) {
                    actionNumber = 2;
                    body = this.macAddress + " " + this.realIpAddress + ";" + this.realPort;
                } else {
                    if(this.ipTable.containsKey(ipAddress)) {
                        body = toolbox.convertIpToString(message.getActionIp()) + " " + this.macAddress + " " + this.realIpAddress + ";" + this.realPort;
                        actionNumber = 3;
                    }
                }
                if(actionNumber > -1) {
                    String[] senderInformation = message.getMessage().split(" ");
                    String[] realInformation = senderInformation[1].split(";");
                    String bodyMessage = this.createMessage(messageSenderIp, actionNumber, "", body);
                    this.send(senderInformation[0], bodyMessage, realInformation[0], Integer.parseInt(realInformation[1]));
                }

                break;

            case 2: //yep, soy yo. BODY: macAddress realIpAddress;realPort
                String[] splitString = message.getMessage().split(" ");
                this.ipTable.put(messageSenderIp, splitString[0]);
                this.addressLocator.put(splitString[0], splitString[1]);
                break;

            case 3: //yep, yo conozco esa dirección IP, es através mío. BODY: ipVirtualAddressSolicitado macAddress realIpAddress;realPort
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
