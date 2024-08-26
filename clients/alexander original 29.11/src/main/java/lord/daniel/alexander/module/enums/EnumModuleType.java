package lord.daniel.alexander.module.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@RequiredArgsConstructor
public enum EnumModuleType {
    SERVER("Server", true),
    COMBAT("Combat", true),
    MOVEMENT("Movement", true),
    PLAYER("Player", true),
    EXPLOIT("Exploit", true),
    WORLD("World", true),
    RENDER("Render", true),
    HUD("Design", true),
    CHAT("Chat", true),
    OPTIONS("Options", true),
    TICKBASE("TickBase", false),
    NETWORK("Network", false),
    GHOST("Ghost", false),
    INPUT("Input", false);

    private final String name;
    private final boolean enabledByDefault;
}
