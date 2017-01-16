package sample.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sample.model.Message;

import java.math.BigInteger;
/**
 * Created by Maciek on 31.12.2016.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ConnectMessage extends Message{
    private BigInteger k;
    private BigInteger n;
    private Integer port;
    private String username;

    public ConnectMessage( BigInteger k, BigInteger n){
        this.k = k;
        this.n = n;
    }
}
