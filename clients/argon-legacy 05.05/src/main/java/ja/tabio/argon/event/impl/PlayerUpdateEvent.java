package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;

public class PlayerUpdateEvent {

    public final Stage stage;
    public final PlayerType type;

    public float rotationYaw, rotationPitch;

    public PlayerUpdateEvent(Stage stage, PlayerType type, float rotationYaw, float rotationPitch) {
        this.stage = stage;
        this.type = type;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
