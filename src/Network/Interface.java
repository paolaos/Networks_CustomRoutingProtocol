package Network;

import Network.Message.Message;
import Network.Toolbox.Toolbox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * The biggest abstract class that covers all basic functionality, such as sockets, ports, virtual names and data structures.
 */
public abstract class Interface extends Thread {
    protected String virtualIpAddress;
    protected String realIpAddress;
    protected String realSendingPort;
    protected String realReceivingPort;
    protected String macAddress;
    protected ServerSocket serverSocket;
    protected Toolbox toolbox;
    protected Map<String, String> addressLocator;
    protected Map<String, String> ipTable;

    public Interface(String threadName, Map<String, String> addressLocator, Map<String, String> ipTable){
        super(threadName);
        this.addressLocator = addressLocator;
        this.ipTable = ipTable;
        this.toolbox = new Toolbox();
    }

    /**
     * Method that orchestrates thread functionality
     */
    public abstract void run();

    /**
     * All requirements passed by the user before the interface starts executing.
     */
    protected abstract void prepare();

    /**
     * In charge of packing an envelope with a message and opening the sending socket for the message.
     * @param receiver Mac address of the interface that will receive the message.
     * @param body the whole message as a string
     * @param realIpAddress the real ip address of the receiver
     * @param realPort the real port of the receiver
     */
    protected synchronized void send(String receiver, String body, String realIpAddress, int realPort) {
        String message = this.macAddress + ";" + receiver + ";" + body + ";";

        try {
            Socket socket = new Socket(realIpAddress, realPort);
            System.out.println("Conectada a servidor.");
            OutputStream os = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            out.writeUTF(message);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * In charge of processing the message's content and take action depending on its action number and its interface type.
     * @param message message to be analyzed
     */
    protected abstract void processMessage(Message message);

}
