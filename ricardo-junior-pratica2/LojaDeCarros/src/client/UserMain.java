package client;

import java.io.IOException;

public class UserMain {
    public static void main(String[] args) {
        try {
            UserInterface usuarios = new UserInterface();
            usuarios.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
