package Network;

import Network.Envelope.Envelope;
import Network.Message.Message;
import Network.Toolbox.Toolbox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

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

    public Interface(String threadName){
        super(threadName);
    }

    public abstract void run();

    protected abstract void prepare();

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

    protected abstract void processMessage(Message message);

}
