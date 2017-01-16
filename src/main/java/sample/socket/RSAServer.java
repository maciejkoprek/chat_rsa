package sample.socket;

import lombok.extern.log4j.Log4j2;
import sample.service.RSAService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j2
public class RSAServer {
    private RSAService rsaService;
    private Integer port;

    public RSAServer(String [] args){
        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
                log.info("Import program parameters");
            } else {
                throw new Exception("Wrong number of parameters");
            }

            rsaService = new RSAService();
            rsaService.generateKeys();
            runServer();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void runServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("Starting server");
            while (true) {
                Socket clientSocket = null;

                clientSocket = serverSocket.accept();
                ConnectedClientThread cliThread = new ConnectedClientThread(clientSocket, rsaService);
                cliThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}