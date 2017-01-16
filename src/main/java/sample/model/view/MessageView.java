package sample.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Created by maciek on 09.01.17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageView {
    private String username;
    private String message;
    private LocalTime createdAt;
    private Boolean error = false;

    public MessageView(String username, String message, LocalTime createdAt){
        this.username = username;
        this.message = message;
        this.createdAt = createdAt;
    }
}
