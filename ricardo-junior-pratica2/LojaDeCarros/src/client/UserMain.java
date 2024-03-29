package client;

import java.io.IOException;

public class UserMain {
    public static void main(String[] args) {
        try {
            /**
             * true --> ENTRA COMO FUNCIONARIO
             * false --> ENTRA COMO USUÁRIO NORMAL
             */
            UserInterface usuarios = new UserInterface(false);
            usuarios.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
