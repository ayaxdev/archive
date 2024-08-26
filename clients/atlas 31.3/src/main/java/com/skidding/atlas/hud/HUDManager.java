package com.skidding.atlas.hud;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.setting.SettingManager;
import com.skidding.atlas.util.render.DrawUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HUDManager extends Manager<HUDFactory> {

    private static volatile HUDManager HUDManager;
    public final List<HUDElement> renderElements = new ArrayList<>();

    public static synchronized HUDManager getSingleton() {
        return HUDManager == null ? HUDManager = new HUDManager() : HUDManager;
    }

    public HUDManager() {
        super(HUDFactory.class);
    }

    public void add(HUDElement hudElement) {
        renderElements.add(hudElement);
        SettingManager.getSingleton().add(hudElement);
    }

    public void remove(HUDElement hudElement) {
        renderElements.remove(hudElement);
        SettingManager.getSingleton().remove(hudElement);
    }

    public void drawElements(boolean designerOpen) {
        for(HUDElement element : renderElements) {
            if(!element.enabled.get())
                continue;

            element.updateSize();

            element.begin();

            try {
                element.draw();

                if(designerOpen)
                    DrawUtil.drawBorderedRectAbsolute(0, 0, element.width, element.height,
                            2f, 0, new Color(0, 0, 0, 100).getRGB(), false);
            } catch (Exception e) {
                AtlasClient.getInstance().logger.error(STR."Something went wrong while drawing \{element.name} element in HUD.", e);
            }

            element.end();
        }
    }



}
