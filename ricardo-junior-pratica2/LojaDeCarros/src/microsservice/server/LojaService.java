package microsservice.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import util.ClientSocket;

public class LojaService {

    public final int PORTA = 1060;

    public final int GATEWAY_PORTA = 1042;

    public final int DATABASE_PORTA = 6156;

    private ServerSocket serverSocket;

    public LojaService() {
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando serviço na porta = " + PORTA);
        lojaService();
    }

    private void lojaService() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
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
                if (msg[0].equals("response")) {

                } else {
                    switch (msg[3]) {
                        case "3": {
                            if (Boolean.parseBoolean(msg[2])) {
                                sendToDB("veiculos;insert;" + msg[4] + ";" + msg[5] + ";" + msg[6] + ";" + msg[7] + ";"
                                        + msg[8] + ";" + msg[9]);
                            } else {
                                sendToGateway("loja;servico;Erro: SEM AUTORIZACAO;" + msg[9]);
                            }
                            break;
                        }
                        case "4": {
                            sendToDB("veiculos;select;" + msg[4] + ";" + msg[5]);
                            break;
                        }
                        case "5": {
                            sendToDB("veiculos;select;-1;" + msg[4]);
                            break;
                        }
                        case "6": {
                            sendToDB("veiculos;select;quantidade;" + msg[4]);
                            break;
                        }
                        case "7": {
                            sendToDB("veiculos;update;compra;" + msg[4] + ";" + msg[5] + ";" + msg[6]);
                            break;
                        }
                        case "8": {
                            if (Boolean.parseBoolean(msg[2])) {
                                sendToDB("veiculos;delete;" + msg[4] + ";" + msg[5]);
                            } else {
                                sendToGateway("loja;servico;Erro: SEM AUTORIZACAO;" + msg[5]);
                            }
                            break;
                        }
                        case "9": {
                            if (Boolean.parseBoolean(msg[2])) {
                                sendToDB("veiculos;update;" + msg[4] + ";" + msg[5] + ";" + msg[6] + ";" + msg[7] + ";"
                                        + msg[8] + ";" + msg[9]);
                            } else {
                                sendToGateway("loja;servico;Erro: SEM AUTORIZACAO;" + msg[9]);
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendToGateway(String mensagem) {
        ClientSocket sendGateway;
        try {
            sendGateway = new ClientSocket(new Socket("localhost", GATEWAY_PORTA));
            sendGateway.sendMessage(mensagem);
            sendGateway.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToDB(String req) {
        ClientSocket send_database;
        try {
            send_database = new ClientSocket(new Socket("localhost", DATABASE_PORTA));
            send_database.sendMessage(req);
            send_database.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
