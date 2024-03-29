package microsservice.gateway;

import java.io.IOException;

public class GatewayMain {
    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        try {
            gateway.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
