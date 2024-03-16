package client;

import util.ClientSocket;
import java.util.Scanner;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;

public class FuncionarioInterface implements Runnable {
    
    private final String ENDERECO_SERVER = "localhost";

    private ClientSocket clientSocket;

    private Scanner scan;

    private Boolean logado;

    private final Boolean ADMIN;

    public FuncionarioInterface() {
        this.scan = new Scanner(System.in);
        this.logado = false;
        this.ADMIN = true;
    }

    @Override
    public void run() {
        String mensagem;
        while ((mensagem = this.clientSocket.getMessage()) != null) {
            if (mensagem.split(" ")[0].equals("status")) {
                logado = Boolean.parseBoolean(mensagem.split(" ")[1]);
            } else {
                System.out.println(
                        "Resposta da loja: " + mensagem);
            }
        }
    }

    private void autenticar() {
        System.out.println("> 1 Entrar\n> 2 Registrar-se");
        System.out.print("> ");
        String op = scan.next();
        if (op.equals("1")) {
            System.out.println("> CPF");
            System.out.print("> ");
            String login = scan.next();
            System.out.println("> Senha");
            System.out.print("> ");
            String senha = scan.next();
            enviar("1;" + login + ";" + senha + ";" + ADMIN);
        } else if (op.equals("2")) {
            String senha;
            String nova_conta = "";
            System.out.println("Registrando\n> CPF");
            System.out.print("> ");
            nova_conta += scan.next() + ";";
            System.out.println("> Senha");
            System.out.print("> ");
            senha = scan.next();
            nova_conta += senha;
            enviar("2;" + nova_conta + ";" + ADMIN);
        }
    }

    private void enviar(String mensagem) {
        this.clientSocket.sendMessage(mensagem);
    }

    private void menu() {
        System.out.println(
                "> 3 [ ADICIONAR CARRD ]\n> 4 [ BUSCAR CARRO ]\n> 5 [ LISTAR CARROS ]\n> 6 [ QUANTIDADE DE CARROS ]\n> 7 [ COMPRAR CARRO ]\n> 8 [ INVESTIR EM RENDA FIXA]\n> 9 [ SIMULAR POUPANÇA ]\n> 10 [ SIMULAR RENDA FIXA ]\n> sair");
    }

    private void messageLoop() {
        String mensagem = "";
        try {
            do {
                Thread.sleep(300);
                if (!logado) {
                    autenticar();
                } else {
                    System.out.println("> LOGADO");
                    menu();
                    System.out.print("> ");
                    mensagem = scan.next();
                    processOption(mensagem);
                }
            } while (!mensagem.equalsIgnoreCase("sair"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processOption(String option) {
        String msg;
        switch (option) {
            case "3":
                msg = "3;";
                System.out.println("> RENAVAM");
                System.out.print("> ");
                msg += this.scan.next() + ";";
                System.out.println("> NOME");
                System.out.print("> ");
                msg += this.scan.next() + ";";
                System.out.println("> CATEGORIA");
                System.out.print("> ");
                msg += this.scan.next() + ";";
                System.out.println("> DATA DE CRIAÇÃO (xxxx-xx-xx)");
                System.out.print("> ");
                msg += this.scan.next() + ";";
                System.out.println("> PREÇO");
                System.out.print("> ");
                msg += this.scan.next();
                enviar(msg);
                break;
            case "4":
                msg = "4;";
                System.out.println("> RENAVAM");
                System.out.print("> ");
                msg += this.scan.next();
                enviar(msg);
                break;
            case "5":
                msg = "5;";
                System.out.println("> LISTANDO...");
                enviar(msg);
                break;
            case "6":
                msg = "6;";
                enviar(msg);
                break;
            case "7":
                msg = "7;";
                System.out.println("> CPF");
                System.out.print("> ");
                msg += this.scan.next() + ";";
                System.out.println("> RENAVAM");
                System.out.print("> ");
                msg += this.scan.next();
                enviar(msg);
                break;
            case "sair":
                System.out.println("Saindo");
                break;
            default:
                System.out.println("comando não achado");
                break;
        }
    }

    public void start() throws IOException, UnknownHostException {
        try {
            clientSocket = new ClientSocket(
                    new Socket(ENDERECO_SERVER, 1025));
            System.out
                    .println("Cliente conectado ao servidor de endereço = " + ENDERECO_SERVER + " na porta = " + 1025);
            new Thread(this).start();
            messageLoop();
        } finally {
            clientSocket.close();
        }
    }

}
