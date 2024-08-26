package com.skidding.atlas.hud;

import com.google.gson.JsonObject;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.hud.util.Side;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.render.CleanF3Module;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.interfaces.Settings;
import com.skidding.atlas.util.java.object.EnumUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

public abstract class HUDElement implements Feature, Settings {

    private final CleanF3Module cleanF3Module = ModuleManager.getSingleton().getByClass(CleanF3Module.class);

    public final String name, description;
    public final Supplier<Boolean> enabled;

    public float x, y, width, height;
    public final int priority;
    public Side side;

    public final SettingFeature<String> horizontal = mode("Horizontal", Side.Horizontal.LEFT.toString(), Side.Horizontal.values())
            .addValueChangeListeners((_, newValue, _, pre) -> {
                if(pre)
                    side = new Side(EnumUtil.getEnumConstantBasedOnString(Side.Horizontal.class, newValue), side.vertical());
            })
            .build();

    public final SettingFeature<String> vertical = mode("Vertical", Side.Vertical.UP.toString(), Side.Vertical.values())
            .addValueChangeListeners((_, newValue, _, pre) -> {
                if(pre)
                    side = new Side(side.horizontal(), EnumUtil.getEnumConstantBasedOnString(Side.Vertical.class, newValue));
            })
            .build();

    public HUDElement(String name, String description,  Supplier<Boolean> enabled, float x, float y, int priority, Side side) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.x = x;
        this.y = y;
        this.priority = priority;
        this.side = side;
    }

    public float scale = 1;
    public boolean drag = false;
    public float prevMouseX = 0F, prevMouseY = 0F;

    public abstract void draw();
    public abstract void updateSize();

    public double getRenderX() {
        return switch (side.horizontal()) {
            case LEFT -> x;
            case MIDDLE_RIGHT -> (new ScaledResolution(mc).getScaledWidth() / 2d) - x;
            case MIDDLE_LEFT -> (new ScaledResolution(mc).getScaledWidth() / 2d) - x - width;
            case RIGHT -> new ScaledResolution(mc).getScaledWidth() - x;
        };
    }

    public void setRenderX(float value) {
        switch (side.horizontal()) {
            case LEFT:
                x += value;
                break;
            case MIDDLE_LEFT:
            case MIDDLE_RIGHT:
            case RIGHT:
                x -= value;
                break;
        }
    }

    public double getRenderY() {
        double renderY = switch (side.vertical()) {
            case UP -> y;
            case MIDDLE -> (new ScaledResolution(mc).getScaledHeight() / 2d) - y;
            case DOWN -> new ScaledResolution(mc).getScaledHeight() - y;
        };

        if(cleanF3Module.isEnabled())
            renderY += cleanF3Module.getY();

        return renderY;
    }

    public void setRenderY(float value) {
        switch (side.vertical()) {
            case UP:
                y += value;
                break;
            case MIDDLE:
            case DOWN:
                y -= value;
                break;
        }
    }

    public void begin() {
        GL11.glPushMatrix();

        if(this.scale != 1F)
            GL11.glScalef(this.scale, this.scale, this.scale);

        GL11.glTranslated(this.getRenderX(), this.getRenderY(), 0F);
    }

    public void end() {
        GL11.glScalef(1f, 1f, 1f);
        GL11.glPopMatrix();
    }

    public String getPreview() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("X", x);
        jsonObject.addProperty("Y", y);
        jsonObject.addProperty("Scale", scale);
        jsonObject.addProperty("Priority", priority);
        jsonObject.addProperty("Horizontal facing", side.horizontal().toString());
        jsonObject.addProperty("Vertical facing", side.vertical().toString());
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {  }
}
