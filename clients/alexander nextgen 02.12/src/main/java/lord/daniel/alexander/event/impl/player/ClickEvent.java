package lord.daniel.alexander.event.impl.player;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@Getter
@Setter
public class ClickEvent extends Event {
    private boolean cancelVanillaClick;
    public ClickEvent(Stage stage) {
        super(stage);
    }
}
