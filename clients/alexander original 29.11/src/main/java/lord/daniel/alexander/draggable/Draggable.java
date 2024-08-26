package lord.daniel.alexander.draggable;

import com.google.gson.JsonObject;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.game.Render2DEvent;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.util.render.RenderUtil;
import org.lwjgl.input.Mouse;

import java.util.function.Supplier;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@Setter
public class Draggable {

    private final String name;
    private float posX, posY;
    private float width, height;
    private final boolean expandable;
    private final float initialWidth, initialHeight;

    private boolean dragging;
    private float xDiff, yDiff;

    private boolean expanding;

    private final Supplier<Boolean> enabled;

    public Draggable(String name, float x, float y, float width, float height) {
        this(name, x, y, width, height, false, () -> true);
    }

    public Draggable(String name, float x, float y, float width, float height, boolean expandable) {
        this(name, x, y, width, height, false, () -> true);
    }

    public Draggable(String name, float x, float y, float width, float height, boolean expandable, Supplier<Boolean> enabled) {
        this.name = name;
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;

        this.initialWidth = width;
        this.initialHeight = height;

        this.expandable = expandable;

        this.enabled = enabled;

        Modification.INSTANCE.getBus().subscribe(this);
    }

    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        if(!this.enabled.get())
            return false;

        if(RenderUtil.isHovered(mouseX, mouseY, posX + width - 10, posY + height - 10, 10, 10) && expandable) {
            expanding = true;
            return true;
        }

        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY - 15, width, height + 30)) {
            dragging = true;
            xDiff = mouseX - posX;
            yDiff = mouseY - posY;
            return true;
        }

        return false;
    }

    public void onDraw(int mouseX, int mouseY) {
        if(!this.enabled.get())
            return;

        if(!Mouse.isButtonDown(0)) {
            dragging = false;
            expanding = false;
            return;
        }

        if(expanding && expandable) {
            float deltaY = mouseY - (posY + height);
            height = Math.max(initialHeight, height + deltaY);
            float deltaX = mouseX - (posX + width);
            width = Math.max(initialWidth, width + deltaX);
        }

        if(dragging) {
            posX = mouseX - xDiff;
            posY = mouseY - yDiff;
        }

    }

    @EventLink
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        if(!Mouse.isButtonDown(0)) {
            dragging = false;
        }
    };

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("X", getPosX());
        object.addProperty("Y", getPosY());
        object.addProperty("Width", getWidth());
        object.addProperty("Height", getHeight());
        return object;
    }

    public void load(JsonObject object) {
        try {
            if (object.has("X"))
                setPosX(object.get("X").getAsFloat());
            if (object.has("Y"))
                setPosY(object.get("Y").getAsFloat());
            if (object.has("Width"))
                setWidth(object.get("Width").getAsFloat());
            if (object.has("Height"))
                setHeight(object.get("Height").getAsFloat());
        } catch (Exception e) {
            // Ignored
        }
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}
