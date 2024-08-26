package lord.daniel.alexander.module.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Written by Daniel. on 21/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@RequiredArgsConstructor
@Getter
public enum EnumModuleType {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    WORLD("World"),
    RENDER("Render"),
    CHAT("Chat"),
    HUD("Design"),
    OPTIONS("Options");

    private final String name;
}
