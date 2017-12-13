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
    private NavigableMap<Date, Integer> bufferCurrentStatus;

    public Router(String threadName, NavigableMap<Date, Integer> bufferCurrentStatus) {
        super(threadName);
        this.toolbox = new Toolbox();
        this.addressLocator = new TreeMap<>();
        this.ipTable = new TreeMap<>();
        buffer = new LinkedList<>();
        this.bufferCurrentStatus = bufferCurrentStatus;
        for(int i = 0; i < 10; i++) {
            BufferNode temp = new BufferNode();
            temp.setId(i);
            temp.setState(BufferNodeState.VACANT);
            temp.setTimestamp(new Date());
            buffer.add(temp);
            bufferCurrentStatus.put(temp.getTimestamp(), temp.getId());

        }


    }

    @Override
    public void run() {
        if(getName().equals("serverActivation")) {
            this.prepare();
            try {
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
                    Date now = new Date();
                    this.buffer.get(position).setTimestamp(now);
                    synchronized (this.bufferCurrentStatus) {
                        this.bufferCurrentStatus.put(now, position);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(getName().equals("logActivation")) try {
                this.sleep(20000);
                while (true) {
                    System.out.println(this.printLog());
                    this.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } else {
                if(getName().equals("messageProcessing")) {
                    while(true) {
                        if(!this.bufferCurrentStatus.isEmpty()) {
                            Map.Entry<Date, Integer> lastEntry;
                            synchronized (this.bufferCurrentStatus) {
                                lastEntry = this.bufferCurrentStatus.lastEntry();
                            }
                            int result = lastEntry.getValue();
                            this.processMessage(this.buffer.get(result).getEnvelope().getMessage());
                            synchronized (this.bufferCurrentStatus) {
                                this.bufferCurrentStatus.remove(lastEntry.getKey());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
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

    @Override
    protected void processMessage(Message message) {
        switch (message.getAction()) {
            case 1: //entra networkAddress macAddress realIpAddress,realSendingPort
                String[] splitString = message.getMessage().split(" ");
                this.addressLocator.put(splitString[0], splitString[1]);
                this.ipTable.put(splitString[1], splitString[2]);
                break;
            case 0: //FORWARD
                try {
                    this.sleep(waitingTime);
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
        int result = this.bufferCurrentStatus.get(this.bufferCurrentStatus.firstKey());
        synchronized (this.bufferCurrentStatus) {
            this.bufferCurrentStatus.remove(this.bufferCurrentStatus.firstKey());
        }

        return result;
    }

    public String printLog(){
        return this.bufferCurrentStatus.toString();
    }


}
