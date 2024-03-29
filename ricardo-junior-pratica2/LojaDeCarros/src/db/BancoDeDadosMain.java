package db;

import java.io.IOException;

public class BancoDeDadosMain {
    public static void main(String[] args) {
        BancoDeDados bancoDeDados = new BancoDeDados();
        try {
            bancoDeDados.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
