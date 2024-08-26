package lord.daniel.alexander.storage.impl;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.draggable.Draggable;
import lord.daniel.alexander.draggable.annotations.DraggableInfo;
import lord.daniel.alexander.draggable.interfaces.IDraggableElement;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.storage.Storage;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.Sys;
import org.reflections.Reflections;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class DraggableStorage extends Storage<Draggable> {

    @Getter
    @Setter
    private static DraggableStorage draggableStorage;

    public void init() {
        Modification.INSTANCE.getBus().subscribe(this);
        final Reflections reflections = new Reflections("lord.daniel.alexander");
        reflections.getTypesAnnotatedWith(DraggableInfo.class).forEach(aClass -> {
            try {
                boolean found = false;
                for(AbstractModule abstractModule : ModuleStorage.getModuleStorage().getList()) {
                    if(abstractModule.getClass() == aClass) {
                        add(((IDraggableElement) abstractModule).getDraggableElements());
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    this.add(((IDraggableElement) aClass.getDeclaredConstructor().newInstance()).getDraggableElements());
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public void onMouseClick(int mouseX, int mouseY, int button) {
        for(Draggable draggable : getList()) {
            if(!draggable.isEnabled())
                continue;

            // To prevent dragging multiple elements at once
            if(draggable.onMouseClick(mouseX, mouseY, button))
                break;
        }
    }

    private FontRenderer font;

    public void onDraw(int mouseX, int mouseY) {
        if(font == null)
            font = FontStorage.getFontStorage().get("Roboto", 19);

        for(Draggable draggable : getList()) {
            if(!draggable.isEnabled())
                continue;

            RenderUtil.drawRect(draggable.getPosX(), draggable.getPosY() - font.FONT_HEIGHT - 2, font.getStringWidth(draggable.getName()) + 4, font.FONT_HEIGHT + 1, new Color(0, 0, 0, 130));
            font.drawStringWithShadow(draggable.getName(), draggable.getPosX() + 2, draggable.getPosY() - font.FONT_HEIGHT - 2 + 0.5f, -1);
            draggable.onDraw(mouseX, mouseY);
        }
    }

}
