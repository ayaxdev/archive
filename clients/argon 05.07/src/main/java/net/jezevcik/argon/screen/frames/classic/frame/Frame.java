package net.jezevcik.argon.screen.frames.classic.frame;

import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.screen.frames.classic.ClassicFramesScreen;
import net.jezevcik.argon.screen.frames.classic.window.Window;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.toggle.Toggleable;
import net.jezevcik.argon.utils.math.GeometryUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Frame {

    private final ClassicFramesScreen parent;

    public final String name;
    public final List<Object> entries = new ArrayList<>();

    private float frameX, frameY;
    private final float entryWidth, entryHeight;

    private boolean dragging = false;
    private double dragX = -1, dragY = -1;

    public Frame(String name, float frameX, float frameY, float entryWidth, float entryHeight, List<?> entries, ClassicFramesScreen parent) {
        this.name = name;
        this.frameX = frameX;
        this.frameY = frameY;
        this.entryWidth = entryWidth;
        this.entryHeight = entryHeight;
        this.parent = parent;

        this.entries.addAll(entries);
    }

    public void render(UiBuilder uiBuilder, int mouseX, int mouseY, float delta) {
        if (dragging) {
            frameX = (float) (mouseX - dragX);
            frameY = (float) (mouseY - dragY);
        }
        
        uiBuilder.rect(frameX, frameY, entryWidth, entryHeight, ClassicFramesScreen.BACK_COLOR, true);

        uiBuilder.text(name)
                .x().centered(frameX, entryWidth).finish()
                .y().centered(frameY, entryHeight).finish()
                .shadow().draw();

        float entryY = frameY + entryHeight;

        for (Object o : entries) {
            if ((!(o instanceof Identifiable identifiable)))
                continue;

            final boolean hovered = GeometryUtils.isInBounds(mouseX, mouseY, frameX, entryY, entryWidth, entryHeight, true);

            uiBuilder.rect(frameX, entryY, entryWidth, entryHeight, hovered ? ClassicFramesScreen.BACK_COLOR_HOVERED : ClassicFramesScreen.BACK_COLOR, true);

            MutableText text = Text.literal(identifiable.getIdentifier(IdentifierType.DISPLAY));

            if (o instanceof Toggleable toggleable) {
                if (toggleable.isEnabled())
                    text = text.setStyle(Style.EMPTY.withBold(true));
            }

            uiBuilder.text(text)
                    .x().centered(frameX, entryWidth).finish()
                    .y().centered(entryY, entryHeight).finish()
                    .shadow().draw();
            
            entryY += entryHeight;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (GeometryUtils.isInBounds(mouseX, mouseY, frameX, frameY, entryWidth, entryHeight, true)) {
            dragging = true;
            dragX = mouseX - frameX;
            dragY = mouseY - frameY;
        }
        
        float entryY = frameY + entryHeight;

        for (Object o : entries) {
            if ((!(o instanceof Identifiable)))
                continue;

            final boolean hovered = GeometryUtils.isInBounds(mouseX, mouseY, frameX, entryY, entryWidth, entryHeight, true);

            if (!hovered) {
                entryY += entryHeight;
                continue;
            }

            if (button == 0 && (o instanceof Toggleable toggleable)) {
                toggleable.toggle();
            } else if (button == 1) {
                parent.window = new Window(parent, o);
                parent.state = ClassicFramesScreen.State.OPENED;
            }

            entryY += entryHeight;
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragX = -1;
            dragY = -1;
            dragging = false;
            return true;
        }
        
        return false;
    }

}
