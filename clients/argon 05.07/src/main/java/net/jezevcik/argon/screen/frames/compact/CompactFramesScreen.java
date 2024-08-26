package net.jezevcik.argon.screen.frames.compact;

import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.screen.SingleCallScreen;
import net.jezevcik.argon.screen.frames.compact.frame.Frame;
import net.jezevcik.argon.screen.frames.compact.window.Window;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompactFramesScreen extends SingleCallScreen {

    public final static int BACK_COLOR = new Color(0, 0, 0, 180).getRGB()
            , BACK_COLOR_HOVERED = new Color(0, 0, 0, 190).getRGB();

    public final List<Frame> frames = new ArrayList<>();

    public State state = State.FRAMES;
    public Window window;

    public CompactFramesScreen() {
        super(Text.literal("Frames"));

        float x = 50;

        for (ModuleCategory category : ModuleCategory.values()) {
            frames.add(new Frame(category.getIdentifier(IdentifierType.DISPLAY).toUpperCase(), x, 50, 100, 16, category.get(), this));
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
                    for (Frame frame : frames) {
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
