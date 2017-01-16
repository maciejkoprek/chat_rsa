package sample.window;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MyPreloader extends Preloader {

    private static final double WIDTH = 400;
    private static final double HEIGHT = 400;

    private Stage preloaderStage;
    private Scene scene;

    private Label progress;

    public MyPreloader() {
        // Constructor is called before everything.
    }

    @Override
    public void init() throws Exception {
        // If preloader has complex UI it's initialization can be done in MyPreloader#init
        Platform.runLater(() -> {
            Label title = new Label("Loading, please wait...");
            title.setTextAlignment(TextAlignment.CENTER);
            progress = new Label("Waiting for connection to server");

            VBox root = new VBox(title, progress);
            root.setAlignment(Pos.CENTER);

            scene = new Scene(root, WIDTH, HEIGHT);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;


        // Set preloader scene and show stage.
        preloaderStage.setScene(scene);
        preloaderStage.show();

        preloaderStage.setOnCloseRequest(e -> {
            System.exit(1);
        });
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                // Called after MyPreloader#start is called.
                break;
            case BEFORE_INIT:
                // Called before WindowChat#init is called.
                break;
            case BEFORE_START:
                // Called after WindowChat#init and before WindowChat#start is called.
                preloaderStage.hide();
                break;
        }
    }
}