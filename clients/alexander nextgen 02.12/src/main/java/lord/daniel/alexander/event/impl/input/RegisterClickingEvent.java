package lord.daniel.alexander.event.impl.input;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@Getter
@Setter
public class RegisterClickingEvent extends Event {
    private boolean cancelVanillaClick;

    public RegisterClickingEvent(Stage stage) {
        super(stage);
    }
}
