package microsservice.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.Cliente;
import model.Veiculo;
import util.Categoria;
import util.ClientSocket;
import util.HashTable.Table;

public class LojaService {

    public final int PORTA = 1060;

    private ServerSocket serverSocket;

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private Table<Veiculo, Integer> veiculos;

    private Table<Cliente, Integer> clientes;

    public LojaService() {
        this.veiculos = new Table<>();
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

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando serviço na porta = " + PORTA);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            new Thread(() -> {
                try {
                    lojaLoop(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void lojaLoop(ClientSocket clientSocket) throws IOException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                switch (msg[1]) {
                    case "3": {
                        // ADICIONAR CARRO
                        System.out.println(
                                "[3] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        if (Boolean.parseBoolean(msg[0])) {
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
                            unicast(clientSocket, "Veiculo Adicionado!");
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
                            String lista = this.veiculos.toStream()
                                    .filter(veiculo -> veiculo.getA_venda() || veiculo.getCliente() == null)
                                    .map(Veiculo::toString)
                                    .collect(Collectors.joining("\n"));
                            unicast(clientSocket, lista);
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
                        int quantidade_carros = (int) this.veiculos.toStream()
                                .filter(veiculo -> veiculo.getA_venda()).count();
                        unicast(clientSocket,
                                "Tem " + quantidade_carros + " veículos cadastrados!");
                        break;
                    }
                    case "7": {
                        // COMPRAR CARRO
                        System.out.println(
                                "[7] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        try {
                            Cliente cliente = this.clientes.BuscarCF(Integer.parseInt(msg[2])).getValor();
                            Veiculo veiculo = this.veiculos.BuscarCF(Integer.parseInt(msg[3])).getValor();
                            veiculo.setA_venda(false);
                            cliente.setVeiculo(veiculo);
                            veiculo.setCliente(cliente);
                            this.clientes.Atualizar(cliente, Integer.parseInt(msg[2]));
                            this.veiculos.Atualizar(veiculo, Integer.parseInt(msg[3]));
                            unicast(clientSocket, "Você adquiriu o carro: " + veiculo.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            unicast(clientSocket, "Cliente e/ou veículo não encontrado!");
                        }
                        break;
                    }
                    case "8": {
                        // APAGAR CARRO
                        System.out.println(
                                "[8] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        if (Boolean.parseBoolean(msg[0])) {
                            try {

                                this.veiculos.Remover(Integer.parseInt(msg[2]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            unicast(clientSocket, "Veiculo Removido!");
                        } else {
                            unicast(clientSocket, "Sem autorização!");
                        }
                        break;
                    }
                    case "9": {
                        // ATUALIZAR CARRO
                        System.out.println(
                                "[9] Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                        if (Boolean.parseBoolean(msg[0])) {
                            try {
                                Categoria nova;
                                Veiculo veiculo_velho = this.veiculos.BuscarCF(Integer.parseInt(msg[2])).getValor();
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
                                        nova = veiculo_velho.getCategoria();
                                        break;
                                }
                                Veiculo veiculo_novo = new Veiculo(msg[2], msg[3], nova, LocalDate.parse(msg[5]),
                                        Double.parseDouble(msg[6]));
                                this.veiculos.Atualizar(veiculo_novo,
                                        Integer.parseInt(veiculo_velho.getRenavam()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            unicast(clientSocket, "Veiculo Atualizado!");
                        } else {
                            unicast(clientSocket, "Sem autorização!");
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
