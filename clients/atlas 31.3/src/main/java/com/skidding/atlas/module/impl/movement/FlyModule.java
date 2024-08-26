package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.impl.player.movement.CalculateCollisionEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

public class FlyModule extends ModuleFeature {

    public final SettingFeature<String> flightMode = mode("Mode", "Motion", new String[]{"Motion", "Air-walk", "Air-jump", "Vulcan", "Verus"}).build();
    public final SettingFeature<String> vulcanMode = mode("Vulcan mode", "Glide", new String[]{"Glide"}).addDependency(flightMode, "Vulcan").build();
    public final SettingFeature<String> verusMode = mode("Verus mode", "Collision", new String[]{"Collision"}).addDependency(flightMode, "Verus").build();

    public final SettingFeature<Float> motionVSpeed = slider("Vertical motion", 0.4f, 0, 5, 1).addDependency(flightMode, "Motion").build();
    public final SettingFeature<Float> motionHSpeed = slider("Horizontal motion", 0.5f, 0, 5, 1).addDependency(flightMode, "Motion").build();

    public final SettingFeature<Boolean> viewBobbing = check("View bobbing", true).build();

    private final TimerUtil timer = new TimerUtil();
    private boolean up;

    public FlyModule() {
        super(new ModuleBuilder("Fly", "Grants the ability to fly through the air", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (viewBobbing.getValue()) {
            getPlayer().cameraYaw = 0.105f;
        }

        switch (flightMode.getValue()) {
            case "Motion" -> {
                getPlayer().motionY = 0.0;

                if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    getPlayer().motionY = motionVSpeed.getValue();
                }

                if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                    getPlayer().motionY = -motionVSpeed.getValue();
                }

                MovementUtil.INSTANCE.setSpeed(motionHSpeed.getValue());
            }
            case "Air-jump" -> {
                if (timer.hasElapsed(545, true)) {
                    getPlayer().jump();
                    getPlayer().onGround = true;
                }
            }
            case "Air-walk" -> {
                getPlayer().motionY = 0.0;
                getPlayer().onGround = true;
            }
            case "Vulcan" -> {
                switch (vulcanMode.getValue()) {
                    case "Glide" -> getPlayer().motionY = getPlayer().ticksExisted % 2 == 0 ? -0.1 : -0.17; // One line Vulcant fly
                }
            }
            case "Verus" -> {
                switch (verusMode.getValue()) {
                    case "Collision" -> {
                        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                            if (getPlayer().onGround) {
                                getPlayer().motionY = 0.42f;
                                up = true;
                            } else if (up) {
                                if (!getPlayer().isCollidedHorizontally) {
                                    getPlayer().motionY = -0.0784000015258789;
                                }
                                up = false;
                            }
                        } else if (getPlayer().ticksExisted % 3 == 0) {
                            getPlayer().motionY = 0.42f;
                        }
                        MovementUtil.INSTANCE.setSpeed(mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.33);
                    }
                }
            }
        }
    }

    @EventHandler
    public final void onCollision(CalculateCollisionEvent calculateCollisionEvent) {
        switch (flightMode.getValue()) {
            case "Verus" -> {
                if (verusMode.getValue().equals("Collision")) {
                    calculateCollisionEvent.boundingBox = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(calculateCollisionEvent.blockPos.getX(), calculateCollisionEvent.blockPos.getY(), calculateCollisionEvent.blockPos.getZ());
                }
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
