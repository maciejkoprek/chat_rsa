package sample.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sample.model.Message;
import sample.model.enums.StatusType;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class StatusMessage extends Message {
    private StatusType status;
}
