package db;

import java.time.LocalDate;

import model.Cliente;
import model.Funcionario;
import model.Veiculo;
import util.Categoria;
import util.ClientSocket;
import util.HashTable.Table;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BancoDeDados {
    
    private Table<Cliente, Integer> clientes;

    private Table<Funcionario, Integer> funcionarios;

    private Table<Veiculo, Integer> veiculos;

    private ServerSocket serverSocket;

    private ClientSocket[] servicos;

    public BancoDeDados(){
        this.servicos = new ClientSocket[2];
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

    private void queries(ClientSocket clientSocket){
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                
            }
        } finally {
            clientSocket.close();
        }
    }

    private void unicast(ClientSocket destinario, String mensagem) {
        // ClientSocket emissor = this.USUARIOS.stream()
        //         .filter(user -> user.getSocketAddress().equals(destinario.getSocketAddress()))
        //         .findFirst().get();
        // emissor.sendMessage(mensagem);
    }

    public void start() throws IOException{
        this.serverSocket = new ServerSocket(6666);
    }

}
