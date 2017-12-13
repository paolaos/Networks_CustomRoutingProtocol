package Network.ExternalNetwork;

import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ExternalNetworkController {
    public synchronized void run() {
        NavigableMap<Date, Integer> currentBufferLog = new TreeMap<>();
        Thread serverActivation;
        Thread logActivation;
        Thread messageProcessing;

        serverActivation = new Router("serverActivation", currentBufferLog);
        logActivation = new Router("logActivation", currentBufferLog);
        messageProcessing = new Router("messageProcessing", currentBufferLog);

        serverActivation.start();
        logActivation.start();
        messageProcessing.start();


    }

}
