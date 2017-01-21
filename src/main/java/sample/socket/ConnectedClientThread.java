package sample.socket;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import sample.model.Message;
import sample.model.enums.StatusType;
import sample.model.impl.ConnectMessage;
import sample.model.impl.ClientContentMessage;
import sample.model.impl.ServerContentMessage;
import sample.model.impl.StatusMessage;
import sample.service.RSAService;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maciek on 14.01.17.
 */
@Log4j2
class ConnectedClientThread extends Thread {
    private static List<ConnectedClientThread> users =  Collections.synchronizedList(new CopyOnWriteArrayList<ConnectedClientThread>());
    private Socket clientSocket;
    private RSAService rsaService;

    private BigInteger clientK;
    private BigInteger clientN;
    private String clientUsername;
    private Integer clientPort;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean running = true;

    private Client client;
    private Thread clientThread;

    public Client getClient(){
        return client;
    }

    ConnectedClientThread(Socket s, RSAService rsaService) {
        this.clientSocket = s;
        this.rsaService = rsaService;
    }

    public void run() {
        try{
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            ConnectMessage connectMessage = (ConnectMessage) this.in.readObject();

            clientK = connectMessage.getK();
            clientN = connectMessage.getN();
            clientPort = connectMessage.getPort();
            clientUsername = connectMessage.getUsername();

            out.writeObject(new ConnectMessage( rsaService.getK(), rsaService.getN()));
            out.flush();

            client = new Client();
            clientThread = new Thread(client);
            clientThread.start();

            ConnectedClientThread.users.add(this);
            log.info("Connected client "+clientUsername + "(All users: "+ ConnectedClientThread.users.size()+")");
            do {

                Message message = (Message)in.readObject();
                if(message instanceof ClientContentMessage){
                    ClientContentMessage inMessage = (ClientContentMessage) message;
                    String decryptMessage = rsaService.decrypt(inMessage.getText());

                    if (StringUtils.isNotEmpty(decryptMessage)) {
                        for(ConnectedClientThread connectedClientThread : ConnectedClientThread.users){
                            connectedClientThread.getClient().sendMessage(decryptMessage, clientUsername);
                        }

                        out.writeObject(new StatusMessage(StatusType.OK));
                        out.flush();
                    }

                    out.writeObject(new StatusMessage(StatusType.ERROR));
                    out.flush();
                }
            } while (running);
        }catch(SocketException | EOFException e){
            ConnectedClientThread.users.remove(this);
            running = false;
            client.running = false;
            log.info("Disconnect client "+clientUsername + "(All users: "+ ConnectedClientThread.users.size()+")");
            clientThread.interrupt();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    class Client implements Runnable {
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Socket socket;
        private boolean running = true;

        public Client() {
        }

        @Override
        public void run() {
            try {
                socket = new Socket("127.0.0.1", clientPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                do {
                    Thread.sleep(250);
                } while (running);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String orginalMessage, String username) throws SocketException, IOException, ClassNotFoundException{
            String encryptMessage = rsaService.encrypt(orginalMessage, clientK, clientN);
            log.info("Server->Client: ("+orginalMessage+") -> "+encryptMessage);
            ServerContentMessage outMessage = new ServerContentMessage(encryptMessage, LocalTime.now(), username);
            out.writeObject(outMessage);
            out.flush();
            StatusMessage inMessage = (StatusMessage) in.readObject();
        }
    }
}