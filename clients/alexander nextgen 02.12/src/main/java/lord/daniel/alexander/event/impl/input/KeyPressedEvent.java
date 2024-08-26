package lord.daniel.alexander.event.impl.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;
import net.minecraft.client.settings.KeyBinding;

@Getter
@Setter
@AllArgsConstructor
public class KeyPressedEvent extends Event {
    private KeyBinding keyBinding;
    private boolean pressed;
}
