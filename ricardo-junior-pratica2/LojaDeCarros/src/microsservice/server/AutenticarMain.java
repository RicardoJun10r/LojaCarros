package microsservice.server;

import java.io.IOException;

public class AutenticarMain {
    public static void main(String[] args) {
        AutenticacaoService autenticacaoService = new AutenticacaoService();
        try {
            autenticacaoService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
