package net.jezevcik.argon.screen.frames.classic;

import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.screen.SingleCallScreen;
import net.jezevcik.argon.screen.frames.classic.frame.Frame;
import net.jezevcik.argon.screen.frames.classic.window.Window;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClassicFramesScreen extends SingleCallScreen {

    public final static int BACK_COLOR = new Color(100, 100, 100).getRGB()
            , BACK_COLOR_HOVERED = new Color(100, 100, 100).brighter().getRGB();

    public final List<net.jezevcik.argon.screen.frames.classic.frame.Frame> frames = new ArrayList<>();

    public State state = State.FRAMES;
    public Window window;

    public ClassicFramesScreen() {
        super(Text.literal("Frames"));

        float x = 50;

        for (ModuleCategory category : ModuleCategory.values()) {
            frames.add(new net.jezevcik.argon.screen.frames.classic.frame.Frame(category.getIdentifier(IdentifierType.DISPLAY).toUpperCase(), x, 50, 100, 20, category.get(), this));
            x += 120;
        }
    }

    public boolean run(ScreenCall screenCall) {
        final CallType callType = screenCall.call();
        final UiBuilder uiBuilder = screenCall.uiBuilder();

        assert client != null;

        if (callType == CallType.KEY_TYPED) {
            if (screenCall.key() == 256 && this.shouldCloseOnEsc()) {
                if (state == State.FRAMES) {
                    this.close();
                } else {
                    state = State.FRAMES;
                    window = null;
                }
                
                return true;
            }
        }

        if (state == State.FRAMES) {
            switch (callType) {
                case DRAW_SCREEN ->
                        frames.forEach(frame -> frame.render(uiBuilder, (int) screenCall.mouseX(), (int) screenCall.mouseY(), screenCall.delta()));

                case MOUSE_CLICK -> {
                    for (net.jezevcik.argon.screen.frames.classic.frame.Frame frame : frames) {
                        if (frame.mouseClicked(screenCall.mouseX(), screenCall.mouseY(), screenCall.key()))
                            return true;
                    }

                    return false;
                }

                case MOUSE_RELEASE -> {
                    for (Frame frame : frames) {
                        if (frame.mouseReleased(screenCall.mouseX(), screenCall.mouseY(), screenCall.key()))
                            return true;
                    }

                    return false;
                }
            }
        } else {
            window.run(screenCall);
        }

        return false;
    }

    public enum State {
        FRAMES, OPENED
    }

}
