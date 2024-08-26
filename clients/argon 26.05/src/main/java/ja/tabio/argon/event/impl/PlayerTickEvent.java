package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;

public class PlayerTickEvent {

    public final Stage stage;
    public final PlayerType type;

    public PlayerTickEvent(Stage stage, PlayerType type) {
        this.stage = stage;
        this.type = type;
    }
}