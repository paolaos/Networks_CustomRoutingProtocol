import Network.ExternalNetwork.ExternalNetworkController;
import Network.InternalNetwork.InternalNetworkController;

import java.util.Scanner;

public class Controller {
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Presione 0 si es una interfaz para redes externas, o 1 si es para redes internas");
        int result = scanner.nextInt();

        if(result == 0) {
            ExternalNetworkController enc = new ExternalNetworkController();
            enc.run();

        } else {
            InternalNetworkController inc = new InternalNetworkController();
            inc.run();

        }
    }

}
