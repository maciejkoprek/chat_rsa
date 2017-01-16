package sample.socket;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import sample.model.Message;
import sample.model.enums.StatusType;
import sample.model.impl.ConnectMessage;
import sample.model.impl.ClientContentMessage;
import sample.model.impl.ServerContentMessage;
import sample.model.impl.StatusMessage;
import sample.model.view.MessageView;
import sample.service.RSAService;
import sample.window.WindowChat;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;

/**
 * Created by maciek on 14.01.17.
 */
@Log4j2
public class RSAClient {
    ClientRunnable clientRunnable;
    ServerRunnable serverRunnable;

    private Integer port;
    private String username;
    private RSAService rsaService;
    private BigInteger serverK;
    private BigInteger serverN;
    private Integer serverPort;

    private WindowChat windowManager;
    private boolean running = false;

    public static void main(String[] args){
        RSAClient rsaClient = new RSAClient(args);
        rsaClient.run();
    }

    public RSAClient(String[] args){
        try {
            if (args.length == 3) {
                serverPort = Integer.parseInt(args[0]);
                port = Integer.parseInt(args[1]);
                username = args[2];
                log.info("Import program parameters");
            } else {
                throw new Exception("Wrong number of parameters");
            }
            rsaService = new RSAService();
            rsaService.generateKeys();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run(){
        clientRunnable = new ClientRunnable();
        serverRunnable = new ServerRunnable();

        Thread serverTheard = new Thread(serverRunnable);
        serverTheard.start();
    }

    public void sendMessage(String orginalMessage){
        clientRunnable.sendMessage(orginalMessage);
    }

    class ServerRunnable implements Runnable{
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private ServerSocket serverSocket;
        public ServerRunnable() {
        }

        @Override
        public void run(){
            try {
                serverSocket = new ServerSocket(port);

                log.info("Create socket server");
                Thread clientTheard = new Thread(clientRunnable);
                clientTheard.start();

                Socket socket = serverSocket.accept();
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                do {
                    Message message = (Message)in.readObject();
                    if(message instanceof ServerContentMessage){
                        ServerContentMessage inMessage = (ServerContentMessage) message;
                        String decryptMessage = rsaService.decrypt(inMessage.getText());

                        if (StringUtils.isNotEmpty(decryptMessage)) {
                            MessageView messageView = new MessageView(inMessage.getUsername(), decryptMessage, inMessage.getCreatedAt());
                            windowManager.getWindowChatController().receiveMessage(messageView);
                        }
                    }

                    out.writeObject(new StatusMessage(StatusType.OK));
                    out.flush();
                } while (isRunning());
            }catch(EOFException e){
                running = false;
                log.info("Disconnect from server.");
                Thread.currentThread().interrupt();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class ClientRunnable implements Runnable {
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Socket socket;

        public ClientRunnable() {
        }

        @Override
        public void run() {
            try {
                socket = new Socket("127.0.0.1", serverPort);
                log.info("Connect to Server");

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(new ConnectMessage(rsaService.getK(), rsaService.getN(), port, username));
                out.flush();

                ConnectMessage connectMessage = (ConnectMessage) in.readObject();
                serverK = connectMessage.getK();
                serverN = connectMessage.getN();

                setRunning(true);
                do {
                    Thread.sleep(250);
                } while (isRunning());
            } catch (SocketException e) {
                setRunning(false);
                log.info("Disconnect from server.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String orginalMessage) {
            try {
                String encryptMessage = rsaService.encrypt(orginalMessage, serverK, serverN);

                ClientContentMessage outMessage = new ClientContentMessage(encryptMessage, LocalTime.now());
                out.writeObject(outMessage);
                out.flush();
                StatusMessage inMessage = (StatusMessage) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public WindowChat getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowChat windowManager) {
        this.windowManager = windowManager;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
