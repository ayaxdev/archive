package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.*;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.module.impl.world.ScaffoldWalkModule;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.RandomizationAlgorithmValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.animation.AnimationUtil;
import lord.daniel.alexander.util.animation.MutableAnimation;
import lord.daniel.alexander.util.entity.EntityUtil;
import lord.daniel.alexander.util.math.time.TimeHelper;
import lord.daniel.alexander.util.rotation.RayCastUtil;
import lord.daniel.alexander.util.rotation.RotationUtil;
import lord.daniel.alexander.util.world.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "KillAura", aliases = {"Aura", "AutoAttack"}, enumModuleType = EnumModuleType.COMBAT)
public class KillAuraModule extends AbstractModule {

    private final ExpandableValue targetExpandable = new ExpandableValue("Targeting", this);
    private final StringModeValue targetMode = new StringModeValue("TargetingMode", this, "Priority", new String[]{"Priority", "Single", "Duels"}).addExpandableParents(targetExpandable);
    private final NumberValue<Float> scanRange = new NumberValue<>("ScanRange", this, 3f, 1f, 6f, 1).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final BooleanValue troughWalls = new BooleanValue("TroughWalls", this, false).addExpandableParents(targetExpandable);
    private final NumberValue<Float> troughWallsScanRange = new NumberValue<>("ThroughWallsScanRange", this, 3f, 1f, 6f, 1).addVisibleCondition(targetMode, false, "Duels").addVisibleCondition(troughWalls::getValue).addExpandableParents(targetExpandable);
    private final BooleanValue prioritizeEntitiesInAttackRange = new BooleanValue("PrioritizeEntitiesInAttackRange", this, true).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final StringModeValue sortMode = new StringModeValue("SortMode", this, "Prediction", new String[]{"FOV", "Distance", "Health", "LivingTime", "Balanced"}).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final BooleanValue players = new BooleanValue("TargetPlayers", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue animals = new BooleanValue("TargetAnimals", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue monsters = new BooleanValue("TargetMonsters", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue invisible = new BooleanValue("TargetInvisible", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue dead = new BooleanValue("TargetDead", this, true).addExpandableParents(targetExpandable);
    
    private final ExpandableValue clicking = new ExpandableValue("Clicking", this);
    public final NumberValue<Float> attackRange = new NumberValue<>("AttackRange", this, 3f, 1f, 6f, 1).addExpandableParents(clicking);
    private final NumberValue<Float> troughWallsAttackRange = new NumberValue<>("ThroughWallsAttackRange", this, 3f, 1f, 6f, 1).addVisibleCondition(troughWalls::getValue).addExpandableParents(clicking);
    private final StringModeValue rangeCalculation = new StringModeValue("RangeCalc", this, "HeadToBody", new String[]{"HeadToBody", "FeetToBody", "BlockToBlock"}).addExpandableParents(clicking);
    private final StringModeValue rangeMode = new StringModeValue("RangeMode", this, "Simple", new String[]{"Simple", "HazeRange"}).addExpandableParents(clicking);
    private final NumberValue<Double> hazeRangeAdjustment = new NumberValue<>("HazeRangeAdjustment", this, 0.5d, 0d, 1d, 2).addVisibleCondition(() -> rangeMode.is("HazeRange")).addExpandableParents(clicking);
    private final NumberValue<Double> maxHazeRange = new NumberValue<>("MaxHazeRange", this, 1d, 0d, 3d, 1).addVisibleCondition(() -> rangeMode.is("HazeRange")).addExpandableParents(clicking);
    private final MultiSelectValue clickEvent = new MultiSelectValue("AttackEvent", this, new String[]{"OnClicking", "OnTick"}, new String[]{"OnClicking", "OnTick", "OnPreUpdate", "OnPostUpdate", "OnPreMotion", "OnPostMotion"}).addExpandableParents(clicking);
    private final RandomizedNumberValue<Integer> attackChance = new RandomizedNumberValue<>("AttackChance", this, 100, 100, 0, 100, 0).addExpandableParents(clicking);
    private final RandomizedNumberValue<Integer> hitLandChance = new RandomizedNumberValue<>("HitLandChance", this, 100, 100, 0, 100, 0).addExpandableParents(clicking);
    private final ExpandableValue cpsCalcExpandable = new ExpandableValue("CPSCalculation", this).addExpandableParents(clicking);
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", this, 10, 1, 20, 0).addExpandableParents(cpsCalcExpandable);
    private final StringModeValue cpsMode = new StringModeValue("CPSMode", this, "Randomize", new String[]{"Randomize", "Static"}).addExpandableParents(cpsCalcExpandable);
    private final RandomizedNumberValue<Float> cpsDeviation = new RandomizedNumberValue<>("CPSDeviation", this, -2f, 2f, -10f, 10f, 1).addExpandableParents(cpsCalcExpandable).addVisibleCondition(() -> cpsMode.is("Randomize"));
    private final BooleanValue reduceInAir = new BooleanValue("ReduceInAir", this, true).addExpandableParents(cpsCalcExpandable);
    private final RandomizedNumberValue<Float> airReduceCps = new RandomizedNumberValue<>("AirCPSReduce", this, 1f, 2f, 0f, 20f, 1).addVisibleCondition(reduceInAir::getValue).addExpandableParents(cpsCalcExpandable);
    private final NumberValue<Float> cpsLimit = new NumberValue<>("CPSLimit", this, 20f, 0f, 50f, 1).addExpandableParents(cpsCalcExpandable);
    private final BooleanValue calculateAfterHit = new BooleanValue("CalculateAfterHit", this, true).addExpandableParents(cpsCalcExpandable);
    private final BooleanValue resetCPS = new BooleanValue("ResetCPS", this, true).addExpandableParents(cpsCalcExpandable);
    private final MultiSelectValue hurtTimes = new MultiSelectValue("HitOnHurtTime", this, new String[]{" 0 ", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 "}, new String[]{" 0 ", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 ", " 10 "}).addExpandableParents(clicking);
    private final BooleanValue perfectHit = new BooleanValue("PerfectHit", this, false).addExpandableParents(clicking);
    private final NumberValue<Integer> perfectHitCorrectness = new NumberValue<>("PerfectHitCorrectness", this, 2, 0, 5).addExpandableParents(clicking).addVisibleCondition(perfectHit::getValue);
    private final BooleanValue leftClickCounter = new BooleanValue("1.8CounterDelay", this, false).addExpandableParents(clicking);
    private final BooleanValue swingOnScan = new BooleanValue("PreSwing", this, false).addExpandableParents(clicking);
    private final BooleanValue rayTrace = new BooleanValue("RayTrace", this, false).addExpandableParents(clicking);
    private final BooleanValue noTimerAttack = new BooleanValue("NoTimerAttack", this, true).addExpandableParents(clicking);
    private final BooleanValue noEatAttack = new BooleanValue("NoEatAttack", this, true).addExpandableParents(clicking);
    private final BooleanValue noContainerAttack = new BooleanValue("NoContainerAttack", this, true).addExpandableParents(clicking);
    private final BooleanValue closeContainer = new BooleanValue("CloseContainer", this, true).addVisibleCondition(noContainerAttack::getValue).addExpandableParents(clicking);
    
    private final ExpandableValue rotationExpandable = new ExpandableValue("Rotations", this);
    private final BooleanValue lockView = new BooleanValue("LockView", this, false).addExpandableParents(rotationExpandable);
    private final StringModeValue lockViewMode = new StringModeValue("LockViewMode", this, "Both", new String[]{"Both", "Yaw", "Pitch"}).addVisibleCondition(lockView::getValue).addExpandableParents(rotationExpandable);
    private final BooleanValue animateRotations = new BooleanValue("AnimateRotations", this, false).addExpandableParents(rotationExpandable);
    private final RandomizedNumberValue<Float> pitchAnimationSpeed = new RandomizedNumberValue<>("PitchAnimationSpeed", this, 3f, 6f, 0f, 25f, 1).addExpandableParents(rotationExpandable).addVisibleCondition(animateRotations::getValue);
    private final RandomizedNumberValue<Float> yawAnimationSpeed = new RandomizedNumberValue<>("YawAnimationSpeed", this, 3f, 6f, 0f, 25f, 1).addExpandableParents(rotationExpandable).addVisibleCondition(animateRotations::getValue);
    private final RandomizedNumberValue<Float> yawDelta = new RandomizedNumberValue<>("MaximumYawDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationExpandable);
    private final RandomizedNumberValue<Float> pitchDelta = new RandomizedNumberValue<>("MaximumPitchDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationExpandable);
    private final BooleanValue roundYawDelta = new BooleanValue("RoundYawDelta", this, true).addExpandableParents(rotationExpandable);
    private final BooleanValue divideByFPS = new BooleanValue("DivideDeltaByFPS", this, false).addExpandableParents(rotationExpandable);
    private final StringModeValue aimVector = new StringModeValue("AimVector", this, "Perfect", new String[]{"Perfect", "Bruteforce", "Smart", "Head", "Neck", "Stomach", "Waist", "Feet"}).addExpandableParents(rotationExpandable);
    private final ExpandableValue randomization = new ExpandableValue("Randomization", this).addExpandableParents(rotationExpandable);
    private final BooleanValue positionRandomization = new BooleanValue("PositionRandomization", this, false).addExpandableParents(randomization);
    private final StringModeValue positionRandomizationMode = new StringModeValue("PositionRandomizationMode", this, "Heuristics", new String[]{"Heuristics", "HeuristicsLight", "Custom"}).addVisibleCondition(positionRandomization::getValue).addExpandableParents(randomization);
    private final RandomizedNumberValue<Float> randomizedXZ = new RandomizedNumberValue<>("RandomizedXZ", this, -0.5f, 0.5f, -1F, 1F, 1).addVisibleCondition(() -> positionRandomization.getValue() && positionRandomizationMode.is("Custom")).addExpandableParents(randomization);
    private final RandomizedNumberValue<Float> randomizedY = new RandomizedNumberValue<>("RandomizedY", this, -0.5f, 0.5f, -1F, 1F, 1).addVisibleCondition(() -> positionRandomization.getValue() && positionRandomizationMode.is("Custom")).addExpandableParents(randomization);
    private final RandomizationAlgorithmValue positionRandomizationAlgorithmValue = new RandomizationAlgorithmValue("PositionRandomizationAlgorithm", this).addVisibleCondition(positionRandomization::getValue).addExpandableParents(randomization);
    private final BooleanValue rotationRandomization = new BooleanValue("RotationRandomization", this, false).addExpandableParents(randomization);
    private final StringModeValue rotationRandomizationMode = new StringModeValue("RotationRandomizationMode", this, "Simple", new String[]{"Simple", "SimpleLegit", "Doubled", "Quadrupled", "Multipoints"}).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);
    private final NumberValue<Float> rotationRandomizationValue = new NumberValue<>("RotationRandomizationValue", this, 0.1f, 0f, 5f).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);
    private final RandomizationAlgorithmValue rotationRandomizationAlgorithmValue = new RandomizationAlgorithmValue("RotationRandomizationAlgorithm", this).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);;
    private final BooleanValue resetRotations = new BooleanValue("ResetRotations", this, true).addExpandableParents(rotationExpandable);
    private final StringModeValue resetMode = new StringModeValue("ResetMode", this, "Silent", new String[]{"Silent", "Visible"}).addVisibleCondition(resetRotations::getValue).addExpandableParents(rotationExpandable);
    private final BooleanValue mouseFix = new BooleanValue("MouseFix", this, true).addExpandableParents(rotationExpandable);
    private final BooleanValue customMouseFix = new BooleanValue("CustomMouseFix", this, false).addExpandableParents(rotationExpandable);
    private final NumberValue<Float> customMouseSpeed = new NumberValue<>("MouseSpeed", this, 0.2f, 0f, 1f, 2).addVisibleCondition(customMouseFix::getValue).addExpandableParents(rotationExpandable);
    private final BooleanValue prediction = new BooleanValue("Prediction", this, false).addExpandableParents(rotationExpandable);
    private final BooleanValue skipRotations = new BooleanValue("SkipUnneededRotations", this, false).addExpandableParents(rotationExpandable);
    private final StringModeValue skipMode = new StringModeValue("SkipMode", this, "Both", new String[]{"Both", "Yaw", "Pitch"}).addVisibleCondition(skipRotations::getValue).addExpandableParents(rotationExpandable);
    private final BooleanValue skipIfNear = new BooleanValue("SkipIfNear", this, true).addVisibleCondition(skipRotations::getValue).addExpandableParents(rotationExpandable);
    private final NumberValue<Float> nearDistance = new NumberValue<>("NearDistance", this, 0.5f, 0f, 0.5f).addVisibleCondition(() -> skipRotations.getValue() && skipIfNear.getValue()).addExpandableParents(rotationExpandable);

    private final ExpandableValue conditions = new ExpandableValue("Conditions", this);
    private final BooleanValue onlyWhenHoldingWeapons = new BooleanValue("OnlyWhenHoldingWeapons", this, false).addExpandableParents(conditions);
    private final BooleanValue onlyWhenHoldingLeftMouse = new BooleanValue("OnlyWhenHoldingLeftMouseButton", this, false).addExpandableParents(conditions);
    private final BooleanValue stopWhenHolding = new BooleanValue("StopWhenHoldingEnderPearls", this, false).addExpandableParents(conditions);

    // Range
    private double curHazeRange = 0D;
    private long lastAttack = 0L;

    // CPS
    private final TimeHelper attackTimer = new TimeHelper();
    private long calcCPS = 0L;

    // Rotations
    private float curYaw, curPitch;
    private boolean silentRotations = false;
    private final TimeHelper simpleLegitTimer = new TimeHelper();
    private boolean simpleLegitUp = true;
    private long simpleLegitDelay = 100;

    // Targeting
    public EntityLivingBase target;
    public final List<Entity> targets = new ArrayList<>();

    // Other
    private ScaffoldWalkModule scaffoldWalkModule;

    @EventLink
    public final Listener<MouseOverEvent> mouseOverEventListener = mouseOverEvent -> {
        if(target != null) {
            mouseOverEvent.setRange(attackRange.getValue());
            mouseOverEvent.setRangeCheck(false);
        }
    };

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(cps.getValue() + "CPS");
    };

    @EventLink
    public final Listener<EntityRendererEvent> rendererEventListener = entityRendererEvent -> {
        if(target == null || shouldDisable()) {
            if(this.silentRotations && resetRotations.getValue()) {
                RotationUtil.resetRotations(PlayerHandler.yaw, PlayerHandler.pitch, resetMode.is("Silent"));
                silentRotations = false;
            }
            return;
        }

        if(this.lockView.getValue()) {
            switch (this.lockViewMode.getValue()) {
                case "Both" -> {
                    mc.thePlayer.rotationYaw = curYaw;
                    mc.thePlayer.rotationPitch = curPitch;
                }
                case "Yaw" -> {
                    mc.thePlayer.rotationYaw = curYaw;
                }
                case "Pitch" -> {
                    mc.thePlayer.rotationPitch = curPitch;
                }
            }
        }
    };

    @EventLink
    public final Listener<RotationEvent> rotationEventListener = event -> {
        if (target == null || shouldDisable()) {
            return;
        }

        Vec3 aimVector = RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1f), target.getEntityBoundingBox());

        switch (this.aimVector.getValue()) {
            case "Smart" -> {
                MovingObjectPosition rayCast = RayCastUtil.rayCast(RotationUtil.getRotation(aimVector), 1);
                if(rayCast != null && rayCast.entityHit == null) {
                    for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25) {
                        for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
                            for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
                                Vec3 tempVec = target.getPositionVector().add(new Vec3(
                                        (target.getEntityBoundingBox().maxX - target.getEntityBoundingBox().minX) * xPercent,
                                        (target.getEntityBoundingBox().maxY - target.getEntityBoundingBox().minY) * yPercent,
                                        (target.getEntityBoundingBox().maxZ - target.getEntityBoundingBox().minZ) * zPercent));
                                float[] rotation = RotationUtil.getRotation(tempVec);
                                MovingObjectPosition rayCast2 = RayCastUtil.rayCast(rotation, attackRange.getValue());
                                if (rayCast2 != null && rayCast2.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                    aimVector = tempVec;
                                }
                            }
                        }
                    }
                }
            }
            case "Bruteforce" -> {
                for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25) {
                    for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
                        for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
                            Vec3 tempVec = target.getPositionVector().add(new Vec3(
                                    (target.getEntityBoundingBox().maxX - target.getEntityBoundingBox().minX) * xPercent,
                                    (target.getEntityBoundingBox().maxY - target.getEntityBoundingBox().minY) * yPercent,
                                    (target.getEntityBoundingBox().maxZ - target.getEntityBoundingBox().minZ) * zPercent));
                            float[] rotation = RotationUtil.getRotation(tempVec);
                            if (RayCastUtil.rayCast(rotation, attackRange.getValue()).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                aimVector = tempVec;
                            }
                        }
                    }
                }
            }

            case "Head" -> aimVector = new Vec3(target.posX, target.posY + target.getEyeHeight(), target.posZ);
            case "Neck" -> aimVector = new Vec3(target.posX, target.posY + target.getEyeHeight() * 0.8f, target.posZ);
            case "Stomach" -> aimVector = new Vec3(target.posX, target.posY + target.getEyeHeight() * 0.5f, target.posZ);
            case "Waist" -> aimVector = new Vec3(target.posX, target.posY + target.getEyeHeight() * 0.4f, target.posZ);
            case "Feet" -> aimVector = new Vec3(target.posX, target.posY, target.posZ);
        }

        double x = aimVector.xCoord - mc.thePlayer.posX;
        double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = aimVector.zCoord - mc.thePlayer.posZ;

        if (prediction.getValue()) {
            final boolean targetIsSprinting = target.isSprinting();
            final boolean playerIsSprinting = mc.thePlayer.isSprinting();

            final float walkingSpeed = 0.10000000149011612f;
            final float targetSpeed = targetIsSprinting ? 1.25f : walkingSpeed;
            final float playerSpeed = playerIsSprinting ? 1.25f : walkingSpeed;

            final float targetPredictedX = (float) ((target.posX - target.prevPosX) * targetSpeed);
            final float targetPredictedZ = (float) ((target.posZ - target.prevPosZ) * targetSpeed);
            final float playerPredictedX = (float) ((mc.thePlayer.posX - mc.thePlayer.prevPosX) * playerSpeed);
            final float playerPredictedZ = (float) ((mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * playerSpeed);

            if (targetPredictedX != 0.0f && targetPredictedZ != 0.0f || playerPredictedX != 0.0f && playerPredictedZ != 0.0f) {
                x += targetPredictedX + playerPredictedX;
                z += targetPredictedZ + playerPredictedZ;
            }
        }

        if(this.positionRandomization.getValue()) {
            switch (positionRandomizationMode.getValue()) {
                case "Heuristics" -> {
                    x += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                    y += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                    z += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                }
                case "HeuristicsLight" -> {
                    final float randomPitch = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(0.015, 0.018);
                    float randomizedPitch = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(0.010, randomPitch);
                    float randomizedYaw = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-0.1, -0.3);
                    x += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-randomizedYaw, randomizedYaw);
                    z += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-randomizedYaw, randomizedYaw);
                    y += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(randomizedPitch, -0.01);
                }
                case "Custom" -> {
                    x += randomizedXZ.getValue();
                    y += randomizedY.getValue();
                    z += randomizedXZ.getValue();
                }
            }
        }

        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(y, d3) * (180 / Math.PI)));
        pitch = MathHelper.clamp_float(pitch, -90, 90);

        if(this.rotationRandomization.getValue()) {
            switch (rotationRandomizationMode.getValue()) {
                case "Simple" -> {
                    yaw += (float) (Math.random() * rotationRandomizationValue.getValue());
                    pitch += (float) (Math.random() * rotationRandomizationValue.getValue());
                }
                case "SimpleLegit" -> {
                    if (simpleLegitUp) {
                        pitch += (float) Math.abs(Math.random() * rotationRandomizationValue.getValue());
                    } else {
                        pitch -= (float) Math.abs(Math.random() * rotationRandomizationValue.getValue());
                    }

                    if (simpleLegitTimer.hasReached(simpleLegitDelay)) {
                        simpleLegitTimer.reset();
                        simpleLegitUp = !simpleLegitUp;
                        simpleLegitDelay = rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomInteger(100, 600);
                    }
                }
                case "Quadrupled" -> {
                    final float random1 = rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(-rotationRandomizationValue.getValue(), rotationRandomizationValue.getValue());
                    final float random2 = rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(-rotationRandomizationValue.getValue(), rotationRandomizationValue.getValue());
                    final float random3 = rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(-rotationRandomizationValue.getValue(), rotationRandomizationValue.getValue());
                    final float random4 = rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(-rotationRandomizationValue.getValue(), rotationRandomizationValue.getValue());
                    yaw += rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(Math.min(random1, random2), Math.max(random1, random2));
                    pitch += rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(Math.min(random3, random4), Math.max(random3, random4));
                }
                case "Doubled" -> {
                    yaw += (float) (Math.random() * rotationRandomizationValue.getValue());
                    pitch += (float) (Math.random() * rotationRandomizationValue.getValue());

                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        yaw += (float) (Math.random() * rotationRandomizationValue.getValue());
                        pitch += (float) (Math.random() * rotationRandomizationValue.getValue());
                    }
                }
                case "Multipoints" -> {
                    pitch += (float) rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(0, rotationRandomizationValue.getValue() * 4);
                    yaw += (float) (Math.random() * rotationRandomizationValue.getValue());
                }
            }
        }

        rotsCalc: {
            if(skipRotations.getValue()) {
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if(movingObjectPosition == null) {
                    curYaw = yaw;
                    curPitch = pitch;
                    break rotsCalc;
                }
                boolean shouldSkip = (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY || movingObjectPosition.entityHit == target) || (skipIfNear.getValue() && getRange(target) <= nearDistance.getValue());
                if(shouldSkip) {
                    if(skipMode.is("Yaw") || skipMode.is("Both"))
                        curYaw = PlayerHandler.yaw;
                    if(skipMode.is("Pitch") || skipMode.is("Both"))
                        curPitch = PlayerHandler.pitch;
                } else {
                    curYaw = yaw;
                    curPitch = pitch;
                }
            } else {
                curYaw = yaw;
                curPitch = pitch;
            }
        }

        float maximumYawDelta = yawDelta.getValue();
        float maximumPitchDelta = pitchDelta.getValue();

        float deltaYaw = curYaw - PlayerHandler.yaw;

        if(roundYawDelta.getValue()) {
            deltaYaw = MathHelper.wrapAngleTo180_float(deltaYaw);
        }

        float deltaPitch = curPitch - PlayerHandler.pitch;

        final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);

        if(divideByFPS.getValue()) {
            deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta) / fps * 4;
            deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta) / fps * 4;
        } else {
            deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta);
            deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta);
        }

        if(animateRotations.getValue()) {
            curYaw = (float) AnimationUtil.move(PlayerHandler.yaw + deltaYaw, PlayerHandler.yaw, AnimationUtil.delta, yawAnimationSpeed.getValue() / 10);
            curPitch = (float) AnimationUtil.move(PlayerHandler.pitch + deltaPitch, PlayerHandler.pitch, AnimationUtil.delta, pitchAnimationSpeed.getValue() / 10);
        } else {
            curYaw = PlayerHandler.yaw + deltaYaw;
            curPitch = PlayerHandler.pitch + deltaPitch;
        }

        if(this.customMouseFix.getValue()) {
            float[] fixed = RotationUtil.applyMouseFix(curYaw, curPitch, this.customMouseSpeed.getValue());
            curYaw = fixed[0];
            curPitch = fixed[1];
        }

        if(this.mouseFix.getValue()) {
            float[] fixed = RotationUtil.applyMouseFix(curYaw, pitch);
            curYaw = fixed[0];
            curPitch = fixed[1];
        }

        event.setYaw(curYaw);
        event.setPitch(curPitch);

        silentRotations = true;
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        switch (updateMotionEvent.getStage()) {
            case PRE -> {
                if(clickEvent.is("OnPreUpdate"))
                    onAttack();
            }
            case MID -> {
                if(clickEvent.is("OnPostUpdate") || clickEvent.is("OnPreMotion"))
                    onAttack();
            }
            case POST -> {
                if(clickEvent.is("OnPostMotion"))
                    onAttack();
            }
        }

        if(target != null) {
            if(rangeCalculation.is("HazeRange")) {
                if (target.hurtTime == 10 && curHazeRange < maxHazeRange.getValue()) {
                    curHazeRange += hazeRangeAdjustment.getValue();
                    lastAttack = System.currentTimeMillis() / 1000;
                }

                if (System.currentTimeMillis() / 1000 - lastAttack >= 2) {
                    curHazeRange = 0;
                }
            }
        }
    };

    @EventLink
    public final Listener<GuiHandleEvent> guiHandleEventListener = guiHandleEvent -> {
        if((noContainerAttack.getValue() && mc.currentScreen instanceof GuiContainer)) {
            if(closeContainer.getValue() && target != null)
                mc.displayGuiScreen(null);
        }
    };

    @EventLink
    public final Listener<OnTickEvent> onTickEventListener = event -> {
        if(clickEvent.is("OnTick"))
            onAttack();
        
        if (mc.theWorld != null) {
            boolean curTargetValid = isValid(target);

            switch (targetMode.getValueAsString()) {
                case "Single" -> {
                    if(target != null && curTargetValid)
                        return;
                }
                case "Duels" -> {
                    Object[] listOfTargets = mc.theWorld.loadedEntityList
                            .stream()
                            .filter(e -> this.isValid(e, false))
                            .sorted(Comparator.comparingDouble(entityx -> (double) mc.thePlayer.getDistanceToEntity(entityx)))
                            .toArray();
                    target = listOfTargets.length == 0 ? null : (EntityLivingBase) listOfTargets[0];
                    return;
                }
            }

            Object[] listOfTargets = null;

            listOfTargets = switch (this.sortMode.getValue()) {
                case "FOV" ->
                        mc.theWorld.loadedEntityList.stream().filter(this::isValid).sorted(Comparator.comparingDouble(this::fov)).toArray();
                case "Health" -> mc.theWorld
                        .loadedEntityList
                        .stream()
                        .filter(this::isValid)
                        .sorted(Comparator.comparingDouble(entityx -> (double) ((EntityLivingBase) entityx).getHealth()))
                        .sorted((Comparator.comparingDouble(object -> {
                            if(!prioritizeEntitiesInAttackRange.getValue())
                                return 0;
                            double range =  mc.thePlayer.getDistanceToEntity(object);
                            return range <= attackRange.getValue() ? 0 : 1;
                        })))
                        .toArray();
                case "LivingTime" -> mc.theWorld
                        .loadedEntityList
                        .stream()
                        .filter(this::isValid)
                        .sorted(Comparator.comparingDouble(entityx -> -entityx.ticksExisted))
                        .sorted((Comparator.comparingDouble(object -> {
                            if(!prioritizeEntitiesInAttackRange.getValue())
                                return 0;
                            double range =  mc.thePlayer.getDistanceToEntity(object);
                            return range <= attackRange.getValue() ? 0 : 1;
                        })))
                        .toArray();
                case "Distance" -> mc.theWorld
                        .loadedEntityList
                        .stream()
                        .filter(this::isValid)
                        .sorted(Comparator.comparingDouble(entityx -> (double) mc.thePlayer.getDistanceToEntity(entityx)))
                        .sorted((Comparator.comparingDouble(object -> {
                            if(!prioritizeEntitiesInAttackRange.getValue())
                                return 0;
                            double range =  mc.thePlayer.getDistanceToEntity(object);
                            return range <= attackRange.getValue() ? 0 : 1;
                        })))
                        .toArray();
                case "Balanced" -> mc.theWorld
                        .loadedEntityList
                        .stream()
                        .filter(this::isValid)
                        .sorted(Comparator.comparingDouble(this::isBestTarget))
                        .sorted((Comparator.comparingDouble(object -> {
                            if(!prioritizeEntitiesInAttackRange.getValue())
                                return 0;
                            double range = mc.thePlayer.getDistanceToEntity(object);
                            return range <= attackRange.getValue() + 0.56 ? 0 : 1;
                        })))
                        .toArray();
                default -> null;
            };

            if (!curTargetValid) {
                target = null;
            }

            if(listOfTargets != null)
                target = listOfTargets.length == 0 ? null : (EntityLivingBase) listOfTargets[0];
        }
    };

    @EventLink
    public final Listener<ClickingEvent> clickingEventListener = e -> {
        if(clickEvent.is("OnClicking"))
            onAttack();
    };

    private boolean shouldDisable() {
        if(onlyWhenHoldingLeftMouse.getValue() && !Mouse.isButtonDown(0))
            return true;

        if(onlyWhenHoldingWeapons.getValue() && (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe)))
            return true;

        if(stopWhenHolding.getValue() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)
            return true;

        if(scaffoldWalkModule == null)
            scaffoldWalkModule = ModuleStorage.getModuleStorage().getByClass(ScaffoldWalkModule.class);

        return scaffoldWalkModule.rotations;
    }

    public void onAttack() {
        if (target == null || shouldDisable() || (noTimerAttack.getValue() && mc.timer.timerSpeed != 1)
                || (noEatAttack.getValue() && mc.thePlayer.isEating())) {

            if (resetCPS.getValue()) calcCPS = 0L;
            return;
        }

        if((noContainerAttack.getValue() && mc.currentScreen instanceof GuiContainer)) {
            if(closeContainer.getValue())
                mc.displayGuiScreen(null);
            else {
                if (resetCPS.getValue()) calcCPS = 0L;
                return;
            }
        }

        if (!calculateAfterHit.getValue())
            calcCPS = calculateCPS();

        double range = getRange(target);
        boolean behindAWall = !mc.thePlayer.canEntityBeSeen(target);
        double attackRangeValue = behindAWall ? (troughWalls.getValue() ? troughWallsAttackRange.getValue() : Double.MAX_VALUE) : attackRange.getValue();
        double scanRangeValue = behindAWall ? (troughWalls.getValue() ? troughWallsScanRange.getValue() : Double.MAX_VALUE) : scanRange.getValue();

        if ((swingOnScan.getValue() ? range <= scanRangeValue : range <= attackRangeValue) && shouldHit()) {
            if (attackChance.getValue() <= attackChance.getRandomizationAlgorithmValue().getRandomizationAlgorithm().getRandomInteger(0, 100)) {
                if (resetCPS.getValue()) calcCPS = 0L;
                return;
            }

            Entity attackTarget = rayTrace.getValue() && mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : target;

            mc.thePlayer.swingItem();

            if (attackTarget != null && range <= attackRangeValue &&
                    hitLandChance.getRandomizationAlgorithmValue().getRandomizationAlgorithm().getRandomInteger(0, 100) <= hitLandChance.getValue()) {
                mc.playerController.attackEntity(mc.thePlayer, attackTarget);
            }

            if (calculateAfterHit.getValue()) calcCPS = calculateCPS();
            attackTimer.reset();
        }
    }

    public boolean shouldHit() {
        if(!this.hurtTimes.getValue().contains(" " + target.hurtTime + " "))
            return false;
        if(this.perfectHit.getValue() && mc.thePlayer.hurtTime == 0 && target.hurtTime > (5 - this.perfectHitCorrectness.getValue()))
            return false;
        if(this.leftClickCounter.getValue() && mc.leftClickCounter != 0)
            return false;
        return this.attackTimer.hasReached(calcCPS);
    }

    public long calculateCPS() {
        float cps = this.cps.getValue();

        if(this.reduceInAir.getValue() && !mc.thePlayer.onGround) {
            cps -= airReduceCps.getValue();
        }

        if (cpsMode.getValue().equals("Randomize")) {
            cps += cpsDeviation.getValue();
        }
        return (long) (1000 / Math.min(cps, this.cpsLimit.getValue()));
    }

    public boolean isValid(Entity entity) {
        return isValid(entity, true);
    }

    public boolean isValid(Entity entity, boolean checkRange) {
        if (entity == null || entity == mc.thePlayer || !(entity instanceof EntityLivingBase) || ((EntityLivingBase) entity).deathTime != 0) {
            return false;
        }

        if(AntiBotModule.isBot((EntityLivingBase) entity))
            return false;

        boolean isPlayer = entity instanceof EntityPlayer;
        boolean isAnimal = entity instanceof EntityAnimal;
        boolean isMob = entity instanceof EntityMob;
        boolean isInvisible = entity.isInvisible();
        boolean isDead = entity.isDead;
        boolean isNotVisible = !mc.thePlayer.canEntityBeSeen(entity);
        double entityRange = getRange(entity);
        boolean isOutOfRange = entityRange > (isNotVisible ? troughWallsScanRange.getValue() : scanRange.getValue());

        return (!isPlayer || players.getValue()) &&
                (!isAnimal || animals.getValue()) &&
                (!isMob || monsters.getValue()) &&
                (!isInvisible || invisible.getValue()) &&
                (!isDead || dead.getValue()) &&
                (!isNotVisible || troughWalls.getValue()) &&
                (!isOutOfRange || !checkRange);
    }

    public double getRange(Entity entity) {
        double range = switch (rangeMode.getValue()) {
            default -> EntityUtil.getRange(entity);
            case "FeetToBody" -> mc.thePlayer.getDistanceToEntity(entity);
            case "BlockToBlock" -> WorldUtil.calculateDistance(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ), new BlockPos(entity.posX, entity.posY + 1, entity.posZ));
        };

        range += switch (rangeCalculation.getValue()) {
            case "HazeRange" -> curHazeRange;
            default -> 0;
        };

        return range;
    }

    private double isBestTarget(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            double distance = mc.thePlayer.getDistanceToEntity(entity);
            double health = ((EntityLivingBase)entity).getHealth();
            double hurtTime = 10.0;
            if (entity instanceof EntityPlayer) {
                hurtTime = ((EntityPlayer)entity).hurtTime;
            }

            return distance * 2.0 + health + hurtTime * 4.0;
        } else {
            return 1000.0;
        }
    }
    private double fov(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            float yaw = RotationUtil.getFovToTarget(
                    entity.posX, entity.posY, entity.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch
            )[0];
            return Math.abs(yaw);
        } else {
            return 1000.0;
        }
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }

    public void reset() {
        calcCPS = 0L;
        target = null;
    }

}
