package net.jezevcik.argon.module.impl;

import com.sun.source.tree.Tree;
import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.config.setting.impl.number.DoubleSetting;
import net.jezevcik.argon.event.impl.ClickReprocessingEvent;
import net.jezevcik.argon.event.impl.LocalPlayerTickEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.extension.impl.ReachExtension;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.processor.impl.rotation.RotationProcessor;
import net.jezevcik.argon.processor.impl.rotation.interfaces.RotationModifier;
import net.jezevcik.argon.processor.impl.rotation.interfaces.Rotator;
import net.jezevcik.argon.processor.impl.rotation.modifier.SensitivityRotationModifier;
import net.jezevcik.argon.processor.impl.rotation.strafe.StrafeCorrector;
import net.jezevcik.argon.processor.impl.rotation.strafe.StrafeMode;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.math.GeometryUtils;
import net.jezevcik.argon.utils.math.MathUtils;
import net.jezevcik.argon.utils.objects.ObjectUtils;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.jezevcik.argon.utils.player.MovementUtils;
import net.jezevcik.argon.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class KillAuraModule extends Module implements Rotator {

    public final Config targets = new Config(this, "Targets", this.config);
    public final ReachExtension scanDistance = new ReachExtension("ScanDistance", 3d, 0d, 10d, 0.1d, targets);
    public final BooleanSetting players = new BooleanSetting("Players", true, targets);
    public final BooleanSetting monsters = new BooleanSetting("Monsters", true, targets);
    public final BooleanSetting animals = new BooleanSetting("Animals", false, targets);

    public final Config rotations = new Config(this, "Rotations", this.config);
    public final BooleanSetting lockView = new BooleanSetting("LockView", true, this.rotations);
    public final BooleanSetting mouseSensitivity = new BooleanSetting("MouseSensitivity", true, this.rotations);
    public final ModeSetting sensitivitySimulation = new ModeSetting("SensitivitySimulationMode", SensitivityRotationModifier.SensitivityMode.REAL,
            SensitivityRotationModifier.SensitivityMode.values(), this.rotations)
            .visibility(SupplierFactory.setting(mouseSensitivity, true, true));

    public final Config movement = new Config(this, "Movement", this.config);
    public final BooleanSetting alignMovement = new BooleanSetting("AlignMovement", false, this.movement);
    public final ModeSetting movementAlignment = new ModeSetting("MovementAlignment", StrafeMode.SILENT, StrafeMode.values(), this.movement);

    public final Config clicking = new Config(this, "Clicking", this.config);
    public final ModeSetting clickMode = new ModeSetting("ClickMode", "Normal", new String[]{"Normal", "SimulateMouse"}, this.clicking);
    public final BooleanSetting rayCast = new BooleanSetting("RayCast", true, this.clicking)
            .visibility(SupplierFactory.reverseSetting(clickMode, "SimulateMouse"));
    public final ModeSetting rayCastMode = new ModeSetting("RayCastMode", "Custom", new String[]{"Custom", "Minecraft"}, this.clicking)
            .visibility(SupplierFactory.setting(rayCast, true, true));
    public final DoubleSetting rayCastRange = new DoubleSetting("RayCastRange", 3d, 0d, 6d, 0.1, this.clicking)
            .visibility(SupplierFactory.setting(rayCastMode, true,"Custom"));
    public final BooleanSetting forceTargetOverRayCast = new BooleanSetting("ForceTargetOverRayCast", false, this.clicking)
            .visibility(SupplierFactory.setting(rayCast, true, true));
    public final BooleanSetting swing = new BooleanSetting("Swing", true, this.clicking)
            .visibility(SupplierFactory.reverseSetting(clickMode, "SimulateMouse"));
    public final ModeSetting swingTime = new ModeSetting("SwingTime", "PostAttack", new String[]{"PreAttack", "PostAttack"}, this.clicking)
            .visibility(SupplierFactory.setting(swing, true, true));

    private LivingEntity target;

    public KillAuraModule() {
        super(ModuleParams.builder()
                .name("KillAura")
                .category(ModuleCategory.COMBAT)
                .build());

        ParekClient.getInstance().processors.getClass(RotationProcessor.class).add(this);
    }

    @EventHandler
    public final void onTick(LocalPlayerTickEvent localPlayerTickEvent) {
        if (!localPlayerTickEvent.pre)
            return;

        final List<LivingEntity> possible = new ArrayList<>();

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity))
                continue;

            if (entity == client.player)
                continue;

            if (!scanDistance.valid(entity))
                continue;

            switch (entity) {
                case PlayerEntity player -> {
                    if (!players.getValue())
                        continue;
                }

                case HostileEntity monster -> {
                    if (!monsters.getValue())
                        continue;
                }

                case AnimalEntity animal -> {
                    if (!animals.getValue())
                        continue;
                }

                default -> {
                    continue;
                }
            };

            possible.add(livingEntity);
        }

        possible.sort((o1, o2) -> (int) ((o1.distanceTo(client.player) - o2.distanceTo(client.player)) * 100));

        if (!possible.isEmpty())
            target = possible.getFirst();
        else
            target = null;
    }

    @EventHandler
    public final void onClick(ClickReprocessingEvent clickReprocessingEvent) {
        if (client.player.getAttackCooldownProgress(0) < 1 || target == null)
            return;

        if (clickMode.getValue().equals("SimulateMouse")) {
            clickReprocessingEvent.clickCallback.left();
            return;
        }

        final Entity attackEntity = rayCast.getValue() ? getRayCastEntity(target) : target;

        if (attackEntity != null) {
            if (swing.getValue() && swingTime.getValue().equals("PreAttack"))
                client.player.swingHand(Hand.MAIN_HAND);

            clickReprocessingEvent.clickCallback.attackEntity(attackEntity);

            if (swing.getValue() && swingTime.getValue().equals("PostAttack"))
                client.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private final RotationProcessor rotationProcessor = ParekClient.getInstance().processors.getClass(RotationProcessor.class);

    private Entity getRayCastEntity(final Entity base) {
        if (!Minecraft.inGame())
            return null;

        assert client.player != null;
        assert client.world != null;

        final HitResult hitResult = rayCastMode.getValue().equals("Normal") ?
                MathUtils.RayCast.getHitResult(client.player
                        , new float[] {rotationProcessor.getYaw(), rotationProcessor.getPitch()}
                        , client.player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
                        , rayCastRange.getValue(), 1F) :
                client.crosshairTarget;

        if (!(hitResult instanceof EntityHitResult entityHitResult))
            return null;

        final Entity rayCast = entityHitResult.getEntity();

        if (forceTargetOverRayCast.getValue() && rayCast != base)
            return null;
        else
            return rayCast;
    }

    @Override
    public float[] rotate(float[] current, boolean tick, float tickDelta) {
        if (!Minecraft.inGame()) {
            return new float[] {-1, -1};
        }

        assert client.player != null;
        assert client.world != null;

        if (target == null) {
            return new float[] {client.player.getYaw(tickDelta), client.player.getPitch(tickDelta)};
        }

        final Vec3d best = GeometryUtils.closestPointInBox(client.player.getEyePos(), target.getBoundingBox());

        return MovementUtils.Math.rotateTo(
                best.getX(),
                best.getY(),
                best.getZ()
        );
    }

    @Override
    public boolean canRotate() {
        return target != null && isEnabled();
    }

    @Override
    public int getPriorityRotations() {
        return 0;
    }

    private final TreeMap<Integer, RotationModifier> modifierMap = new TreeMap<>();

    @Override
    public TreeMap<Integer, RotationModifier> getModifiers() {
        if (modifierMap.isEmpty()) {
            modifierMap.put(0, new SensitivityRotationModifier() {
                @Override
                public boolean isEnabled() {
                    return mouseSensitivity.getValue();
                }

                @Override
                protected SensitivityMode getSensitivityMode() {
                    return ObjectUtils.getEnumByString(SensitivityMode.class, sensitivitySimulation.getValue());
                }
            });
        }

        return modifierMap;
    }

    @Override
    public int getFlags() {
        return lockView.getValue() ? RotationProcessor.RotatorFlags.NON_SILENT : 0;
    }

    private StrafeCorrector strafeCorrector;

    @Override
    public StrafeCorrector getCorrector() {
        if (alignMovement.getValue()) {
            try {
                final StrafeMode strafeMode = ObjectUtils.getEnumByString(StrafeMode.class, movementAlignment.getValue());

                if (this.strafeCorrector == null || this.strafeCorrector.getClass() != strafeMode.corrector)
                    this.strafeCorrector = strafeMode.corrector.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                ParekClient.LOGGER.error("Failed to update strafe corrector", e);
            }

            return strafeCorrector;
        } else
            return null;
    }

    @Override
    public void onToggle(boolean pre) {
        if (!pre)
            target = null;
    }
}
