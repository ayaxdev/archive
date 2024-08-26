package net.jezevcik.argon.module.impl;

import com.google.common.collect.ImmutableMap;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.screen.frames.classic.ClassicFramesScreen;
import net.jezevcik.argon.screen.frames.compact.CompactFramesScreen;
import net.jezevcik.argon.utils.reflection.ClassUtils;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import oshi.annotation.concurrent.Immutable;

import java.util.HashMap;
import java.util.Map;

public class FramesModule extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", "Classic", new String[] {"Classic", "Compact"}, this.config);

    private static final Map<String, Class<? extends Screen>> SCREEN_MAP = ImmutableMap.of(
            "Classic", ClassicFramesScreen.class,
            "Compact", CompactFramesScreen.class
    );

    private final Map<String, Screen> initializedScreenMap = new HashMap<>();

    public FramesModule() {
        super(ModuleParams.builder()
                .name("Frames")
                .category(ModuleCategory.RENDER)
                .key(GLFW.GLFW_KEY_RIGHT_SHIFT)
                .build());

        for (Class<?> klass : SCREEN_MAP.values()) {
            if (!ClassUtils.hasParameterlessPublicConstructor(klass))
                throw new IllegalArgumentException("Class must have a valid constructor!");
        }
    }

    @Override
    public void onEnable() {
        initializedScreenMap.clear();

        try {
            if (!initializedScreenMap.containsKey(mode.getValue()))
                initializedScreenMap.put(mode.getValue(), SCREEN_MAP.get(mode.getValue()).getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            ParekClient.LOGGER.error("There was an error creating frames screen object!", e);
        }

        client.setScreen(initializedScreenMap.get(mode.getValue()));

        try {
            setEnabled(false);
        } catch (Exception e) {
            ParekClient.LOGGER.error("Failed to reset Frames module state", e);
        }
    }

}
