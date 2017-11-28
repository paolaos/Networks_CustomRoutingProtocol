import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

public class Dispatcher {
    private Map<String, String> treeMap;

    public Dispatcher(){
        treeMap = new TreeMap<>();
        this.load();
    }
    private void load(){
        File file = new File("dispatcher.txt");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null)
                treeMap.put(line.split(" ")[0], line.split(" ")[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String fetch(String macAddress) {
        return treeMap.get(macAddress);
    }


}
