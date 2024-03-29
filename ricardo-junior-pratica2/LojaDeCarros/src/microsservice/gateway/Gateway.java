package microsservice.gateway;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.ClientSocket;
import util.Sessao;

public class Gateway {

    public final int PORTA = 1042;

    private final String ENDERECO_SERVER = "localhost";

    private ServerSocket serverSocket;

    private ClientSocket[] servicos = new ClientSocket[2];

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private final Map<SocketAddress, Sessao> SESSAO = new HashMap<>();

    public Gateway() {
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor na porta = " + PORTA);
        this.servicos[0] = new ClientSocket(new Socket(ENDERECO_SERVER, 1050));
        System.out.println("Conectado ao serviço de autenticação");
        this.servicos[1] = new ClientSocket(new Socket(ENDERECO_SERVER, 1060));
        System.out.println("Conectado ao serviço da loja");
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            this.SESSAO.put(clientSocket.getSocketAddress(), new Sessao(false, false));
            new Thread(() -> {
                try {
                    gatewayLoop(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private Boolean autenticar(ClientSocket clientSocket) {
        if (this.SESSAO.get(clientSocket.getSocketAddress()).getLogado())
            return true;
        else
            return false;
    }

    private void gatewayLoop(ClientSocket clientSocket) throws IOException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                if (msg[0].equals("autenticar")) {
                    if (msg[1].equals("servico")) {
                        System.out.println(
                                "[autenticar-servico] Mensagem de " + clientSocket.getSocketAddress() + ": "
                                        + mensagem);
                        ClientSocket destinatario = this.USUARIOS.stream()
                                .filter(c -> c.getSocketAddress().toString().equals(msg[4]))
                                .findFirst().get();
                        boolean logado = msg[2].contains("true");
                        this.SESSAO.put(destinatario.getSocketAddress(),
                                new Sessao(logado, Boolean.parseBoolean(msg[3])));
                        unicast(destinatario, mensagem);
                    } else if (msg[1].equals("cliente")) {
                        System.out.println(
                                "[autenticar-cliente] Mensagem de " + clientSocket.getSocketAddress() + ": "
                                        + mensagem);
                        // mensagem = autenticar;cliente;ADMIN;1;login;senha;socketAddress
                        unicast(this.servicos[0], mensagem + ";" + clientSocket.getSocketAddress());
                    }
                } else if (msg[0].equals("loja")) {
                    if (msg[1].equals("servico")) {
                        System.out.println(
                                "[loja-servico] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        ClientSocket destinatario = this.USUARIOS.stream()
                                .filter(c -> c.getSocketAddress().toString().equals(msg[3]))
                                .findFirst().get();
                        unicast(destinatario, mensagem);
                    } else if (msg[1].equals("cliente")) {
                        if (autenticar(clientSocket)) {
                            System.out.println(
                                    "[loja-cliente] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            unicast(this.servicos[1], mensagem);
                        } else {
                            System.out.println(
                                    "[loja-cliente] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            unicast(clientSocket, "ACESSO NEGADO!");
                        }
                    }
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void unicast(ClientSocket destinario, String mensagem) {
        ClientSocket emissor = this.USUARIOS.stream()
                .filter(user -> user.getSocketAddress().equals(destinario.getSocketAddress()))
                .findFirst().get();
        emissor.sendMessage(mensagem);
    }

}
