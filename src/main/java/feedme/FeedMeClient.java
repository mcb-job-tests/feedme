package feedme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class FeedMeClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    void createSocketConnection(String ip, int port) throws IOException, InterruptedException {

        while (true) {
            try {
                clientSocket = new Socket(ip, port);
                if (clientSocket != null && clientSocket.isConnected()){
                    break;
                };
            }
            catch (IOException e) {
                Thread.sleep(200);
            }
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    Socket getClientSocket() {
        return clientSocket;
    }
}
