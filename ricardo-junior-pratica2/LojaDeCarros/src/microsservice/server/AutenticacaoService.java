package microsservice.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import model.Cliente;
import model.Funcionario;
import util.ClientSocket;
import util.HashTable.Table;

public class AutenticacaoService {

    public final int PORTA = 1050;

    private ServerSocket serverSocket;

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private Table<Cliente, Integer> clientes;

    private Table<Funcionario, Integer> funcionarios;

    public AutenticacaoService() {
        this.clientes = new Table<>();
        this.funcionarios = new Table<>();
        this.clientes.Adicionar(new Cliente("1234", "123"), Integer.parseInt("1234"));
        this.clientes.Adicionar(new Cliente("4567", "456"), Integer.parseInt("4567"));
        this.clientes.Adicionar(new Cliente("7891", "789"), Integer.parseInt("7891"));
        this.funcionarios.Adicionar(new Funcionario("1047", "147"), Integer.parseInt("1047"));
        this.funcionarios.Adicionar(new Funcionario("2058", "258"), Integer.parseInt("2058"));
        this.funcionarios.Adicionar(new Funcionario("3069", "369"), Integer.parseInt("3069"));
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando serviço de autenticação na porta = " + PORTA);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            new Thread(() -> {
                try {
                    clientMessageLoop(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket) throws IOException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                switch (msg[2]) {
                    case "1": {
                        // AUTENTICAR
                        System.out.println(
                                "[1] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        try {
                            if (Boolean.parseBoolean(msg[3])) {
                                Funcionario funcionario = this.funcionarios.BuscarCF(Integer.parseInt(msg[4]))
                                        .getValor();
                                if (funcionario != null) {
                                    if (msg[5].equals(funcionario.getSenha())) {
                                        unicast(clientSocket, "autenticar;servico;status true;true" + msg[6]);
                                    } else {
                                        unicast(clientSocket, "autenticar;servico;status false;false" + msg[6]);
                                    }
                                } else {
                                    unicast(clientSocket, "autenticar;servico;status false;false" + msg[6]);
                                }
                            } else {
                                Cliente cliente = this.clientes.BuscarCF(Integer.parseInt(msg[4]))
                                        .getValor();
                                if (cliente != null) {
                                    if (msg[5].equals(cliente.getSenha())) {
                                        unicast(clientSocket, "autenticar;servico;status true;false" + msg[6]);
                                    } else {
                                        unicast(clientSocket, "autenticar;servico;status false;false" + msg[6]);
                                    }
                                } else {
                                    unicast(clientSocket, "autenticar;servico;status false;false" + msg[6]);
                                }
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "2": {
                        // CRIAR CONTA USUARIO
                        System.out.println(
                                "[2] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        try {
                            if (Boolean.parseBoolean(msg[0])) {
                                this.funcionarios.Adicionar(new Funcionario(msg[2], msg[3]),
                                        Integer.parseInt(msg[2]));
                                unicast(clientSocket, "Funcionário Criado!");
                            } else {
                                this.clientes.Adicionar(new Cliente(msg[2], msg[3]),
                                        Integer.parseInt(msg[2]));
                                unicast(clientSocket, "Cliente Criado!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        System.out.println(
                                "Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        break;
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
