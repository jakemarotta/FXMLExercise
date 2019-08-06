package fxmlexercise.completed;

import java.io.Serializable;
import java.util.HashMap;

public class FrameState implements Serializable {

    // Change this to the value calculated by the "serialver" tool when you're done writing the class.
    private static final long serialVersionUID = 1L;

    private HashMap<String, Serializable> data = new HashMap<>();

    public FrameState(CompletedFrameController controller) {
        // Put each value that you want to save into "data"
    }

    public void apply(CompletedFrameController controller) {
        // Set each property of the controller or child selectors.
    }

}
