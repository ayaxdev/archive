package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.component.AutoClickerComponent;
import com.skidding.atlas.event.impl.client.TargetCheckEvent;
import com.skidding.atlas.event.impl.player.movement.KeepSprintEvent;
import com.skidding.atlas.event.impl.player.rotation.RotationEvent;
import com.skidding.atlas.event.impl.client.TargetSetterEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.RotationProcessor;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.impl.CheckBuilder;
import com.skidding.atlas.setting.builder.impl.ModeBuilder;
import com.skidding.atlas.setting.builder.impl.SliderBuilder;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.minecraft.player.RotationUtil;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import io.github.racoondog.norbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public final class KillAuraModule extends ModuleFeature {

    public final SettingFeature<Float> minCPS = new SliderBuilder("Minimum CPS", 9, 0, 20, 1).build();
    public final SettingFeature<Float> maxCPS = new SliderBuilder("Maximum CPS", 12, 0, 20, 1).build();

    public final SettingFeature<Float> hitChance = new SliderBuilder("Hit chance", 99, 0, 100, 0).build();

    public final SettingFeature<Float> range = new SliderBuilder("Range", 3, 3, 6, 1).build();

    public final SettingFeature<String> sortingMode = new ModeBuilder("Sorting", "Range", new String[]{"Range", "Health", "Armor", "Hurt-time", "Angle"}).build();

    public final SettingFeature<Boolean> players = new CheckBuilder("Players", true).build();
    public final SettingFeature<Boolean> monsters = new CheckBuilder("Monsters", true).build();
    public final SettingFeature<Boolean> animals = new CheckBuilder("Animals", false).build();
    public final SettingFeature<Boolean> switchTargets = new CheckBuilder("Switch targets", false).build();

    public final SettingFeature<String> rotationMode = new ModeBuilder("Rotations", "Closest", new String[]{"Closest", "Intave", "Intave-Safe"}).build();
    public final SettingFeature<Boolean> gcdFix = new CheckBuilder("GCD fix", true).addDependency(rotationMode, "Closest").build();
    public final SettingFeature<Boolean> resetRotation = new CheckBuilder("Rotation reset", true).build();
    public final SettingFeature<String> rotationResetMode = new ModeBuilder("Reset mode", "Silent", new String[]{"Silent", "Visible"}).addDependency(resetRotation, true).build();

    public final SettingFeature<Boolean> keepSprint = new CheckBuilder("Keep sprint", false).build();

    private Pair<Entity, Vec3> target;
    private boolean rotated = false;
    private float lastYaw = Float.MIN_VALUE,
            lastPitch = Float.MIN_VALUE;
    private final TimerUtil randomizationTimer
            = new TimerUtil();
    private final RotationProcessor rotationProcessor =
            ProcessorManager.getSingleton().getByClass(RotationProcessor.class);

    public KillAuraModule() {
        super(new ModuleBuilder("KillAura", "Automatically attacks entities in close proximity", ModuleCategory.COMBAT));

        this.components.add(new AutoClickerComponent(() -> target != null, this::getCps, this::onClick));
    }

    @EventHandler
    public void onSprint(KeepSprintEvent keepSprintEvent) {
        if (keepSprint.getValue()) {
            keepSprintEvent.cancelled = true;
        }
    }

    @EventHandler
    public void onRotation(RotationEvent rotationEvent) {
        if (target != null) {
            target.setValue(RotationUtil.getBestLookVector(getPlayer().getPositionEyes(1F),
                    target.getKey().getEntityBoundingBox()));
            if (target.getKey().isDead || getPlayer().getPositionEyes(1.0f).distanceTo(target.getValue()) > range.getValue())
                target = null;
        }

        if (target == null || switchTargets.getValue()) {
            final ArrayList<Pair<Entity, Vec3>> possibleTargets = new ArrayList<>();

            for (Entity entity : getWorld().loadedEntityList) {
                if (!validEntityType(entity))
                    continue;

                // Do faster,
                // Euclidean distance calculation between positions to eliminate 99% of non-targetable entities
                if (getPlayer().getDistanceToEntity(entity) > (range.getValue() + 2.5F))
                    continue;

                final Vec3 bestVec = RotationUtil.getBestLookVector(getPlayer().getPositionEyes(1F),
                        entity.getEntityBoundingBox());
                if (getPlayer().getPositionEyes(1.0f).distanceTo(bestVec) > range.getValue())
                    continue;

                final TargetCheckEvent targetCheckEvent = new TargetCheckEvent((EntityLivingBase) entity);
                AtlasClient.getInstance().eventPubSub.publish(targetCheckEvent);

                if (targetCheckEvent.allow)
                    possibleTargets.add(new MutablePair<>(entity, bestVec));
            }

            switch (sortingMode.getValue()) {
                default ->
                        possibleTargets.sort(Comparator.comparingDouble(o -> getPlayer().getPositionEyes(1.0f).distanceTo(o.getRight())));
                case "Armor" ->
                        possibleTargets.sort(Comparator.comparingDouble(o -> ((EntityLivingBase) o.getKey()).getTotalArmorValue()));
                case "Angle" ->
                        possibleTargets.sort(Comparator.comparingDouble(o -> RotationUtil.calculateAngleToEntity(o.getKey())));
                case "Health" ->
                        possibleTargets.sort(Comparator.comparingDouble(o -> ((EntityLivingBase) o.getKey()).getHealth()));
                case "Hurt-time" ->
                        possibleTargets.sort(Comparator.comparingDouble(o -> ((EntityLivingBase) o.getKey()).hurtTime));
            }

            if (!possibleTargets.isEmpty()) {
                target = possibleTargets.getFirst();
            }
        }

        if (target != null) {
            if (lastYaw == Float.MIN_VALUE || lastPitch == Float.MIN_VALUE) {
                lastYaw = rotationEvent.rotationYaw;
                lastPitch = rotationEvent.rotationPitch;
            }

            float[] finalRotations = switch (rotationMode.getValue()) {
                case "Intave", "Intave-Safe" -> {
                    Vec3 aimVector = target.getRight();

                    if (rotationMode.getValue().equalsIgnoreCase("Intave-Safe") && randomizationTimer.hasElapsed(100, true)) {
                        aimVector = aimVector.addVector(ThreadLocalRandom.current().nextDouble(-0.14, 0.14),
                                ThreadLocalRandom.current().nextDouble(-0, 0.14),
                                ThreadLocalRandom.current().nextDouble(-0.14, 0.14));
                    }

                    final float[] rotations = RotationUtil.getRotation(aimVector);
                    final float[] fixedRotations = RotationUtil.INSTANCE.applyMinecraftSensitivity(rotations[0], rotations[1], false);

                    boolean necessary = mc.objectMouseOver == null || mc.objectMouseOver.entityHit != target.getKey();

                    if (getPlayer().getDistanceToEntity(target.getKey()) < 1.5d)
                        necessary = true;

                    if (necessary) {
                        yield fixedRotations;
                    } else {
                        yield new float[]{lastYaw,
                                MathUtil.clamp(lastPitch, -90, 90)};
                    }
                }
                case "Closest" -> {
                    final Vec3 aimVector = target.getRight();
                    final float[] rotations = RotationUtil.getRotation(aimVector);

                    if (gcdFix.getValue()) {
                        final float[] fixedRotations = RotationUtil.INSTANCE.applyMinecraftSensitivity(rotations[0], rotations[1], false);

                        rotations[0] = fixedRotations[0];
                        rotations[1] = fixedRotations[1];
                    }

                    yield rotations;
                }
                default -> new float[]{rotationEvent.rotationYaw, rotationEvent.rotationPitch};
            };

            lastYaw = finalRotations[0];
            lastPitch = finalRotations[1];

            rotationEvent.rotationPitch = finalRotations[1];
            rotationEvent.rotationYaw = finalRotations[0];

            rotated = true;
        } else if (rotated) {
            RotationUtil.INSTANCE.resetRotations(rotationProcessor.getRotationYaw(), rotationProcessor.getRotationPitch(), rotationResetMode.getValue().equalsIgnoreCase("Silent"));
            rotated = false;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSetTarget(TargetSetterEvent targetSetterEvent) {
        if (target != null)
            targetSetterEvent.setNextTarget((EntityLivingBase) target.getKey());
    }

    private float getCps() {
        return (float) MathUtil.interpolate(minCPS.getValue(), maxCPS.getValue(), Math.random());
    }

    private boolean onClick() {
        if (ThreadLocalRandom.current().nextInt(0, 101) > hitChance.getValue()) {
            return false;
        }

        mc.clickMouse();

        return true;
    }

    private boolean validEntityType(Entity e) {
        if (e == null || getWorld() == null || getPlayer() == null)
            return false;

        if (!(e instanceof EntityLivingBase))
            return false;

        if (((EntityLivingBase) e).getHealth() == 0)
            return false;

        if (e instanceof EntityPlayer && e != getPlayer() && players.getValue())
            return true;

        if (e instanceof EntityMob && monsters.getValue())
            return true;

        return e instanceof EntityAnimal && animals.getValue();
    }


    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        target = null;
    }

}
