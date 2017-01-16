package sample;

import com.sun.javafx.application.LauncherImpl;
import sample.window.MyPreloader;
import sample.window.WindowChat;

public class MainClinet {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(WindowChat.class, MyPreloader.class, args);
    }
}