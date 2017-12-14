package Network.ExternalNetwork;

import Network.Envelope.Envelope;
import Network.Envelope.InternalEnvelope;
import Network.Interface;
import Network.Message.Message;
import Network.Toolbox.Toolbox;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Router extends Interface {

    private int waitingTime;
    private List<BufferNode> buffer;
    private NavigableMap<Date, Integer> currentBufferLog;

    public Router(String threadName, Map<String, String> addressLocator, Map<String, String> ipTable, NavigableMap<Date, Integer> currentBufferLog, List<BufferNode> buffer) {
        super(threadName, addressLocator, ipTable);
        this.buffer = buffer;
        this.currentBufferLog = currentBufferLog;

    }

    public void run() {
        if(getName().equals("serverActivation")) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Digite el puerto para recibir mensajes. ");
                this.realReceivingPort = scanner.next();
                serverSocket = new ServerSocket(Integer.parseInt(this.realReceivingPort));
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Ya me conecté. ");
                    DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                    String inputLine = dataIn.readUTF();
                    clientSocket.close();
                    System.out.println("Me llegó esto: " + inputLine);
                    int position = this.canBeStored();
                    if (position == -1)
                        position = this.forceASpot();

                    String[] inputContent = inputLine.split(";");
                    Envelope envelope;
                    envelope = new InternalEnvelope();
                    envelope.setSender(inputContent[0]);
                    envelope.setReceiver(inputContent[1]);
                    envelope.setMessage(toolbox.convertStringToMessage(inputContent[2]));
                    this.buffer.get(position).setEnvelope(envelope);
                    this.buffer.get(position).setState(BufferNodeState.BUSY);
                    Date now = new Date();
                    this.buffer.get(position).setTimestamp(now);
                    synchronized (this) {
                        this.currentBufferLog.put(now, position);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(getName().equals("logActivation")) try {
                sleep(35000);
                while (true) {
                    System.out.println(this.printLog());
                    sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } else {
                if(getName().equals("messageProcessing")) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.prepare();
                    this.currentBufferLog.clear();
                    while(true) {
                        try {
                        if(!this.currentBufferLog.isEmpty()) {
                            Map.Entry<Date, Integer> lastEntry;
                            synchronized (this) {
                                lastEntry = this.currentBufferLog.lastEntry();
                                int result = lastEntry.getValue();
                                this.processMessage(this.buffer.get(result).getEnvelope().getMessage());
                                this.currentBufferLog.remove(lastEntry.getKey());
                                this.buffer.get(result).setState(BufferNodeState.VACANT);
                            }
                        }

                        sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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
        this.realSendingPort = scanner.next();
        System.out.println("Cuál es su puerto para recibir mensajes?");
        this.realReceivingPort = scanner.next();
        System.out.println("De cuánto quiere delimitar el tiempo en servicio de cada paquete (en milisegundos)? ");
        this.waitingTime = scanner.nextInt();

    }

    protected void processMessage(Message message) {
        switch (message.getAction()) {
            case 2: //entra networkAddress macAddress realIpAddress,realSendingPort
                String[] splitString = message.getMessage().split(" ");
                this.addressLocator.put(splitString[0], splitString[1]);
                this.ipTable.put(splitString[1], splitString[2]);
                break;
            case 0: //FORWARD message
                try {
                    sleep(waitingTime);
                    int[] requestedIp = message.getReceiverIp();
                    String requestedNetwork = String.valueOf(requestedIp[0]) + "." + String.valueOf(requestedIp[1]) + ".0.0";
                    if(this.addressLocator.containsKey(requestedNetwork)) {
                        String requestedAddress = this.addressLocator.get(requestedNetwork);
                        String[] realAddressAndPort = this.ipTable.get(requestedAddress).split(",");
                        this.send(requestedAddress, toolbox.convertMessageToString(message), realAddressAndPort[0], Integer.parseInt(realAddressAndPort[1]));

                    } else {
                        System.err.println("Couldn't find address " + requestedNetwork);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                break;
        }
    }

    private int canBeStored(){
        int result = -1;
        for(BufferNode bn : buffer) {
            if(bn.getState() == BufferNodeState.VACANT) {
                result = bn.getId();
                break;
            }
        }

        return result;
    }

    private int forceASpot(){
        int result = this.currentBufferLog.get(this.currentBufferLog.firstKey());
        synchronized (this) {
            this.currentBufferLog.remove(this.currentBufferLog.firstKey());
        }

        return result;
    }

    private String printLog(){
        return this.currentBufferLog.toString();
    }


}
