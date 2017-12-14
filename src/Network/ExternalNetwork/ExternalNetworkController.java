package Network.ExternalNetwork;
import java.util.Date;
import java.util.*;

public class ExternalNetworkController {
    public synchronized void run() {
        Map<String, String> ipTable = new TreeMap<>();
        Map<String, String> addressLocator = new TreeMap<>();
        NavigableMap<Date, Integer> currentBufferLog = new TreeMap<>();
        List<BufferNode> buffer = new LinkedList<>();
        for(int i = 0; i < 10; i++) {
            BufferNode temp = new BufferNode();
            temp.setId(i);
            temp.setState(BufferNodeState.VACANT);
            temp.setTimestamp(new Date());
            buffer.add(temp);
            currentBufferLog.put(temp.getTimestamp(), temp.getId());

        }

        Thread serverActivation;
        Thread logActivation;
        Thread messageProcessing;

        serverActivation = new Router("serverActivation", addressLocator, ipTable, currentBufferLog, buffer);
        logActivation = new Router("logActivation", addressLocator, ipTable, currentBufferLog, buffer);
        messageProcessing = new Router("messageProcessing", addressLocator, ipTable, currentBufferLog, buffer);

        serverActivation.start();
        logActivation.start();
        messageProcessing.start();


    }

}
