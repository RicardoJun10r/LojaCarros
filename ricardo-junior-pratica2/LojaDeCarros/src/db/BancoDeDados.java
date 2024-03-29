package db;

import java.time.LocalDate;

import model.Cliente;
import model.Funcionario;
import model.Veiculo;
import util.Categoria;
import util.ClientSocket;
import util.HashTable.Table;

import java.io.IOException;
import java.lang.NullPointerException;
import java.net.ServerSocket;
import java.net.Socket;

public class BancoDeDados {

    private final int PORTA = 6156;

    private final int AUTENTICAR_SERVICO_PORTA = 1050;

    private Table<Cliente, Integer> clientes;

    private Table<Funcionario, Integer> funcionarios;

    private Table<Veiculo, Integer> veiculos;

    private ServerSocket serverSocket;

    public BancoDeDados() {
        this.clientes = new Table<>();
        this.veiculos = new Table<>();
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
        this.veiculos.Adicionar(new Veiculo("7842", "HB20", Categoria.ECONOMICO, LocalDate.of(2022, 6, 20), 23000.0),
                Integer.parseInt("7842"));
        this.veiculos.Adicionar(
                new Veiculo("1112", "Ecosport", Categoria.EXECUTIVO, LocalDate.of(2022, 7, 15), 30000.0),
                Integer.parseInt("1112"));
        this.veiculos.Adicionar(new Veiculo("1212", "HR-V", Categoria.EXECUTIVO, LocalDate.of(2021, 9, 5), 290000.0),
                Integer.parseInt("1212"));
    }

    public void database() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            new Thread(() -> {
                queries(clientSocket);
            }).start();
        }
    }

    private void queries(ClientSocket clientSocket) {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                System.out.println("Mensagem recebida de [ " + clientSocket.getSocketAddress() + " ] = " + mensagem);
                switch (msg[0]) {
                    case "funcionario": {
                        switch (msg[1]) {
                            case "select": {
                                if (selectFuncionario(msg[2], msg[3]) != null) {
                                    sendToAutenticarServico("response;login;true;" + msg[4]);
                                } else {
                                    sendToAutenticarServico("response;login;false;" + msg[4]);
                                }
                                break;
                            }
                            case "insert": {
                                insertFuncionario(msg[2], msg[3]);
                                sendToAutenticarServico("response;criado;" + msg[4]);
                                break;
                            }
                            default:
                                System.out.println("ERRO[BancoDeDados]: " + mensagem);
                                break;
                        }
                        break;
                    }
                    case "cliente": {
                        switch (msg[1]) {
                            case "select": {
                                System.out.println("select: " + mensagem);
                                if (selectCliente(msg[2], msg[3]) != null) {
                                    System.out.println("response;login;true;" + msg[4]);
                                    sendToAutenticarServico("response;login;true;" + msg[4]);
                                } else {
                                    System.out.println("response;login;false;" + msg[4]);
                                    sendToAutenticarServico("response;login;false;" + msg[4]);
                                }
                                break;
                            }
                            case "insert": {
                                insertCliente(msg[2], msg[3]);
                                sendToAutenticarServico("response;criado;" + msg[4]);
                                break;
                            }
                            default:
                                System.out.println("ERRO[BancoDeDados]: " + mensagem);
                                break;
                        }
                        break;
                    }
                    case "veiculos": {
                        break;
                    }
                    default:
                        break;
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendToAutenticarServico(String mensagem) {
        ClientSocket response;
        try {
            response = new ClientSocket(new Socket("localhost", AUTENTICAR_SERVICO_PORTA));
            response.sendMessage(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Funcionario selectFuncionario(String login, String password) {
        try {
            Funcionario funcionario = this.funcionarios.BuscarCF(Integer.parseInt(login)).getValor();
            if (funcionario.getSenha().equals(password)) {
                return funcionario;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cliente selectCliente(String login, String password) {
        try {
            Cliente cliente = this.clientes.BuscarCF(Integer.parseInt(login)).getValor();
            if (cliente.getSenha().equals(password)) {
                return cliente;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertFuncionario(String login, String password) {
        this.funcionarios.Adicionar(new Funcionario(login, password), Integer.parseInt(login));
    }

    private void insertCliente(String login, String password) {
        this.clientes.Adicionar(new Cliente(login, password), Integer.parseInt(login));
    }

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(PORTA);
        database();
    }

}
