package sample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by Maciek on 31.12.2016.
 */
@Data
@AllArgsConstructor
public abstract class Message implements Serializable, Cloneable {
}
