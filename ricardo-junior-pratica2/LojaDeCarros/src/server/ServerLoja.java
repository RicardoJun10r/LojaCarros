package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Cliente;
import model.Funcionario;
import model.Veiculo;
import util.Categoria;
import util.ClientSocket;
import util.Sessao;
import util.HashTable.Table;

public class ServerLoja {

    public final int PORTA = 1025;

    private ServerSocket serverSocket;

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private final Map<SocketAddress, Sessao> SESSAO = new HashMap<>();

    private Table<Veiculo, Integer> veiculos;

    private Table<Cliente, Integer> clientes;

    private Table<Funcionario, Integer> funcionarios;

    public ServerLoja() {
        this.veiculos = new Table<>();
        this.clientes = new Table<>();
        this.funcionarios = new Table<>();
        this.clientes.Adicionar(new Cliente("1234", "123"), Integer.parseInt("1234"));
        this.clientes.Adicionar(new Cliente("4567", "456"), Integer.parseInt("4567"));
        this.clientes.Adicionar(new Cliente("7891", "789"), Integer.parseInt("7891"));
        this.funcionarios.Adicionar(new Funcionario("1047", "147"), Integer.parseInt("1047"));
        this.funcionarios.Adicionar(new Funcionario("2058", "258"), Integer.parseInt("2058"));
        this.funcionarios.Adicionar(new Funcionario("3069", "369"), Integer.parseInt("3069"));
        this.veiculos.Adicionar(new Veiculo("1111", "Gol", Categoria.ECONOMICO, LocalDate.of(2022, 1, 1), 25000.0),
                Integer.parseInt("1111"));
        this.veiculos.Adicionar(new Veiculo("2222", "Palio", Categoria.ECONOMICO, LocalDate.of(2021, 12, 15), 22000.0),
                Integer.parseInt("2222"));
        this.veiculos.Adicionar(new Veiculo("3333", "Onix", Categoria.ECONOMICO, LocalDate.of(2022, 2, 10), 28000.0),
                Integer.parseInt("3333"));
        this.veiculos.Adicionar(
                new Veiculo("4444", "Civic", Categoria.INTERMEDIARIO, LocalDate.of(2022, 3, 5), 35000.0),
                Integer.parseInt("4444"));
        this.veiculos.Adicionar(
                new Veiculo("5555", "Corolla", Categoria.INTERMEDIARIO, LocalDate.of(2021, 11, 20), 38000.0),
                Integer.parseInt("5555"));
        this.veiculos.Adicionar(new Veiculo("6666", "Fiesta", Categoria.ECONOMICO, LocalDate.of(2022, 4, 12), 20000.0),
                Integer.parseInt("6666"));
        this.veiculos.Adicionar(
                new Veiculo("7777", "Cruze", Categoria.INTERMEDIARIO, LocalDate.of(2022, 2, 28), 32000.0),
                Integer.parseInt("7777"));
        this.veiculos.Adicionar(new Veiculo("8888", "Fit", Categoria.ECONOMICO, LocalDate.of(2022, 5, 8), 27000.0),
                Integer.parseInt("8888"));
        this.veiculos.Adicionar(
                new Veiculo("9999", "Fusion", Categoria.INTERMEDIARIO, LocalDate.of(2021, 10, 10), 40000.0),
                Integer.parseInt("9999"));
        this.veiculos.Adicionar(new Veiculo("1010", "HB20", Categoria.ECONOMICO, LocalDate.of(2022, 6, 20), 23000.0),
                Integer.parseInt("1010"));
        this.veiculos.Adicionar(
                new Veiculo("1112", "Ecosport", Categoria.EXECUTIVO, LocalDate.of(2022, 7, 15), 30000.0),
                Integer.parseInt("11011"));
        this.veiculos.Adicionar(new Veiculo("1212", "HR-V", Categoria.EXECUTIVO, LocalDate.of(2021, 9, 5), 290000.0),
                Integer.parseInt("1212"));
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor na porta = " + PORTA);
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
                if (!autenticar(clientSocket)) {
                    System.out.println("NÃO LOGADO!");
                    switch (msg[1]) {
                        case "sair": {
                            // SAIR
                            System.out.println(
                                    "[sair] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            break;
                        }
                        case "1": {
                            // AUTENTICAR
                            System.out.println(
                                    "[1] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            try {
                                if (Boolean.parseBoolean(msg[0])) {
                                    Funcionario funcionario = this.funcionarios.BuscarCF(Integer.parseInt(msg[2]))
                                            .getValor();
                                    if (funcionario != null) {
                                        if (msg[3].equals(funcionario.getSenha())) {
                                            this.SESSAO.put(clientSocket.getSocketAddress(), new Sessao(true, true));
                                            unicast(clientSocket, "status true");
                                        } else {
                                            unicast(clientSocket, "status false");
                                        }
                                    } else {
                                        unicast(clientSocket, "status false");
                                    }
                                } else {
                                    Cliente cliente = this.clientes.BuscarCF(Integer.parseInt(msg[2]))
                                            .getValor();
                                    if (cliente != null) {
                                        if (msg[3].equals(cliente.getSenha())) {
                                            this.SESSAO.put(clientSocket.getSocketAddress(), new Sessao(true, true));
                                            unicast(clientSocket, "status true");
                                        } else {
                                            unicast(clientSocket, "status false");
                                        }
                                    } else {
                                        unicast(clientSocket, "status false");
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
                } else {
                    System.out.println("LOGADO!");
                    switch (msg[1]) {
                        case "sair": {
                            // SAIR
                            System.out.println(
                                    "[sair] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            this.SESSAO.put(clientSocket.getSocketAddress(), new Sessao(false, false));
                            unicast(clientSocket, "status false");
                            break;
                        }
                        case "3": {
                            // ADICIONAR CARRO
                            System.out.println(
                                    "[3] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            if(Boolean.parseBoolean(msg[0])){
                                try {
                                    Categoria nova;
                                    switch (msg[4]) {
                                        case "1":
                                            nova = Categoria.ECONOMICO;
                                            break;
                                        case "2":
                                            nova = Categoria.INTERMEDIARIO;
                                            break;
                                        case "3":
                                            nova = Categoria.EXECUTIVO;
                                            break;
                                        default:
                                            nova = Categoria.ECONOMICO;
                                            break;
                                    }
                                    this.veiculos.Adicionar(
                                            new Veiculo(msg[2], msg[3], nova, LocalDate.parse(msg[5]),
                                                    Double.parseDouble(msg[6])),
                                            Integer.parseInt(msg[2]));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                unicast(clientSocket, "Veiculo Criado!");
                            } else {
                                unicast(clientSocket, "Sem autorização!");
                            }
                            break;
                        }
                        case "4": {
                            // BUSCAR VEICULO
                            System.out.println(
                                    "[4] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            try {
                                Veiculo veiculo = this.veiculos.BuscarCF(Integer.parseInt(msg[2])).getValor();
                                unicast(clientSocket, veiculo.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                unicast(clientSocket, "Veiculo =[ " + msg[2] + " ] não encontrado!");
                            }
                            break;
                        }
                        case "5": {
                            // LISTAR VEICULOS
                            System.out.println(
                                    "[5] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            try {
                                unicast(clientSocket, this.veiculos.Listar());
                            } catch (Exception e) {
                                e.printStackTrace();
                                unicast(clientSocket, "Lista vazia");
                            }
                            break;
                        }
                        case "6": {
                            // QUANTIDADE DE CARROS
                            System.out.println(
                                    "[6] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            unicast(clientSocket,
                                    "Tem " + this.veiculos.Quantidade().toString() + " veículos cadastrados!");
                            break;
                        }
                        case "7": {
                            // COMPRAR CARRO
                            System.out.println(
                                    "[6] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            try {
                                Cliente cliente = this.clientes.BuscarCF(Integer.parseInt(msg[2])).getValor();
                                Veiculo veiculo = this.veiculos.BuscarCF(Integer.parseInt(msg[3])).getValor();
                                veiculo.setA_venda(false);
                                cliente.setVeiculo(veiculo);
                                veiculo.setCliente(cliente);
                                this.clientes.Atualizar(cliente, Integer.parseInt(msg[2]));
                                this.veiculos.Atualizar(veiculo, Integer.parseInt(msg[3]));
                                unicast(clientSocket, "Você adquiriu o carro!");
                            } catch (Exception e) {
                                e.printStackTrace();
                                unicast(clientSocket, "Cliente e/ou veículo não encontrado!");
                            }
                            break;
                        }
                        default:
                            System.out.println(
                                    "Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                            break;
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
