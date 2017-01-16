package sample.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sample.model.Message;

import java.time.LocalTime;

/**
 * Created by maciek on 31.12.16.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientContentMessage extends Message {
    private String text;
    private LocalTime createdAt;
}
