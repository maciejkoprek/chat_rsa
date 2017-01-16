package sample.window;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import sample.socket.RSAClient;

@Getter
public class WindowChat extends Application {
    private WindowChatController windowChatController;
    private RSAClient client;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("chat_window.fxml"));
        Parent root = loader.load();

        windowChatController = loader.<WindowChatController>getController();
        windowChatController.setWindowChat(this);

        primaryStage.setTitle("Chat Window - "+ WindowChat.args[2]);
        primaryStage.setScene(new Scene(root, 650, 350));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            System.exit(1);
        });
    }

    @Override
    public void init() throws Exception {
        args = getParameters().getRaw().toArray(new String[getParameters().getRaw().toArray().length]);

        client = new RSAClient(args);
        client.setWindowManager(this);
        client.run();
        do{
            if(client.isRunning()){
                break;
            }
            Thread.sleep(1000);
        }while(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
