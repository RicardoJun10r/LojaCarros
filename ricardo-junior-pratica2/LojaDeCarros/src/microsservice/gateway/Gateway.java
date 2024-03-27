package microsservice.gateway;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Cliente;
import model.Funcionario;
import model.Veiculo;
import util.Categoria;
import util.ClientSocket;
import util.Sessao;
import util.HashTable.Table;

public class Gateway {

    public final int PORTA = 1042;

    private final String ENDERECO_SERVER = "localhost";

    private ServerSocket serverSocket;

    private ClientSocket[] clientSocket = new ClientSocket[2];

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private final List<ClientSocket> SERVICOS = new LinkedList<>();

    private final Map<SocketAddress, Sessao> SESSAO = new HashMap<>();

    public Gateway() {
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor na porta = " + PORTA);
        this.clientSocket[0] = new ClientSocket(new Socket(ENDERECO_SERVER, 1050));
        System.out.println("Conectado ao serviço de autenticação");
        this.clientSocket[1] = new ClientSocket(new Socket(ENDERECO_SERVER, 1060));
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
                    clientMessageLoop(clientSocket);
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

    private void clientMessageLoop(ClientSocket clientSocket) throws IOException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                if (msg[0].equals("autenticar")) {
                    if (msg[1].equals("servico")) {

                    } else if (msg[1].equals("cliente")) {

                    }
                } else if (msg[0].equals("loja")) {
                    if (msg[1].equals("servico")) {

                    } else if (msg[1].equals("cliente")) {

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
