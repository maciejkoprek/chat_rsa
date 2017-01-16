package sample.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import sample.model.view.MessageView;

import java.time.format.DateTimeFormatter;


public class WindowChatController {
    @FXML
    private TextField inputmessagetarget;

    @FXML
    private TextArea outputmessagestarget;

    private WindowChat windowChat;

    @FXML
    protected void handleSendButtonAction(ActionEvent event) {
        sendMessage();
    }

    @FXML
    public void onEnter(KeyEvent e){
        if(e.getCode().equals(KeyCode.ENTER))
            sendMessage();
    }

    private void sendMessage(){
        String message = inputmessagetarget.getText();
        if(StringUtils.isNotEmpty(message)){
            windowChat.getClient().sendMessage(message);
            inputmessagetarget.clear();
        }
    }

    public void receiveMessage(MessageView messageView){
        outputmessagestarget.appendText(messageFormat(messageView));
    }

    public void setWindowChat(WindowChat windowChat) {
        this.windowChat = windowChat;
    }

    private String messageFormat(MessageView messageView){
        return new StringBuilder("\n[")
                .append(messageView.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .append("] ")
                .append(messageView.getUsername())
                .append(" : ")
                .append(messageView.getMessage()).toString();
    }
}
