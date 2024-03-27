package microsservice.server;

import java.io.IOException;

public class Loja {
    
    public static void main(String[] args) {
        try {
            ServerLoja serverLoja = new ServerLoja();
            serverLoja.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
