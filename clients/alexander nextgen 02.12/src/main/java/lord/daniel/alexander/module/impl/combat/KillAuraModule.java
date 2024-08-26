package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import lord.daniel.alexander.event.impl.client.BackgroundRunEvent;
import lord.daniel.alexander.event.impl.input.MouseOverEvent;
import lord.daniel.alexander.event.impl.player.ClickEvent;
import lord.daniel.alexander.event.impl.player.RotationEvent;
import lord.daniel.alexander.event.impl.player.UpdateEvent;
import lord.daniel.alexander.handler.input.ClickHandler;
import lord.daniel.alexander.handler.player.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectNumberValue;
import lord.daniel.alexander.settings.impl.mode.RandomizationAlgorithmValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.util.animation.AnimationUtil;
import lord.daniel.alexander.util.math.prediction.PredictedPlayer;
import lord.daniel.alexander.util.math.time.MSTimer;
import lord.daniel.alexander.util.player.combat.CombatUtil;
import lord.daniel.alexander.util.player.rotation.RayCastUtil;
import lord.daniel.alexander.util.player.rotation.RotationUtil;
import lord.daniel.alexander.util.world.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CreateModule(name = "KillAura", category = EnumModuleType.COMBAT)
public class KillAuraModule extends AbstractModule {

    private final ExpandableValue targetExpandable = new ExpandableValue("Targeting", this);
    private final StringModeValue targetMode = new StringModeValue("TargetingMode", this, "Priority", new String[]{"Priority", "Single", "Duels"}).addExpandableParents(targetExpandable);
    private final NumberValue<Float> scanRange = new NumberValue<Float>("ScanRange", this, 3f, 1f, 6f, 1).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final BooleanValue troughWalls = new BooleanValue("TroughWalls", this, false).addExpandableParents(targetExpandable);
    private final NumberValue<Float> troughWallsScanRange = new NumberValue<Float>("ThroughWallsScanRange", this, 3f, 1f, 6f, 1).addVisibleCondition(targetMode, false, "Duels").addVisibleCondition(troughWalls::getValue).addExpandableParents(targetExpandable);
    private final BooleanValue prioritizeEntitiesInAttackRange = new BooleanValue("PrioritizeEntitiesInAttackRange", this, true).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final StringModeValue sortMode = new StringModeValue("SortMode", this, "Prediction", new String[]{"FOV", "Distance", "Health", "LivingTime", "Balanced"}).addVisibleCondition(targetMode, false, "Duels").addExpandableParents(targetExpandable);
    private final BooleanValue players = new BooleanValue("TargetPlayers", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue animals = new BooleanValue("TargetAnimals", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue monsters = new BooleanValue("TargetMonsters", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue invisible = new BooleanValue("TargetInvisible", this, true).addExpandableParents(targetExpandable);
    private final BooleanValue dead = new BooleanValue("TargetDead", this, true).addExpandableParents(targetExpandable);

    private final ExpandableValue rotationsExpandable = new ExpandableValue("Rotations", this);
    private final BooleanValue skipRotations = new BooleanValue("SkipUnneededRotations", this, false).addExpandableParents(rotationsExpandable);
    private final StringModeValue skipMode = new StringModeValue("SkipMode", this, "Both", new String[]{"Both", "Yaw", "Pitch"}).addVisibleCondition(skipRotations::getValue).addExpandableParents(rotationsExpandable);
    private final BooleanValue skipIfNear = new BooleanValue("SkipIfNear", this, true).addVisibleCondition(skipRotations::getValue).addExpandableParents(rotationsExpandable);
    private final NumberValue<Float> nearDistance = new NumberValue<>("NearDistance", this, 0.5f, 0f, 0.5f).addVisibleCondition(() -> skipRotations.getValue() && skipIfNear.getValue()).addExpandableParents(rotationsExpandable);
    private final BooleanValue animateRotations = new BooleanValue("AnimateRotations", this, false).addExpandableParents(rotationsExpandable);
    private final RandomizedNumberValue<Float> pitchAnimationSpeed = new RandomizedNumberValue<>("PitchAnimationSpeed", this, 3f, 6f, 0f, 25f, 1).addExpandableParents(rotationsExpandable).addVisibleCondition(animateRotations::getValue);
    private final RandomizedNumberValue<Float> yawAnimationSpeed = new RandomizedNumberValue<>("YawAnimationSpeed", this, 3f, 6f, 0f, 25f, 1).addExpandableParents(rotationsExpandable).addVisibleCondition(animateRotations::getValue);
    private final RandomizedNumberValue<Float> yawDelta = new RandomizedNumberValue<>("MaximumYawDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationsExpandable);
    private final RandomizedNumberValue<Float> pitchDelta = new RandomizedNumberValue<>("MaximumPitchDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationsExpandable);
    private final BooleanValue roundYawDelta = new BooleanValue("RoundYawDelta", this, true).addExpandableParents(rotationsExpandable);
    private final BooleanValue divideByFPS = new BooleanValue("DivideDeltaByFPS", this, false).addExpandableParents(rotationsExpandable);
    private final BooleanValue mouseFix = new BooleanValue("MouseFix", this, true).addExpandableParents(rotationsExpandable);
    private final BooleanValue customMouseFix = new BooleanValue("CustomMouseFix", this, false).addExpandableParents(rotationsExpandable);
    private final NumberValue<Float> customMouseSpeed = new NumberValue<Float>("MouseSpeed", this, 0.2f, 0f, 1f, 2).addVisibleCondition(customMouseFix::getValue).addExpandableParents(rotationsExpandable);
    private final StringModeValue aimVector = new StringModeValue("AimVector", this, "Perfect", new String[]{"Perfect", "Bruteforce", "Smart", "Head", "Neck", "Stomach", "Waist", "Feet"}).addExpandableParents(rotationsExpandable);
    private final BooleanValue prediction = new BooleanValue("Prediction", this, false).addExpandableParents(rotationsExpandable);
    private final NumberValue<Integer> futureTicks = new NumberValue<>("FutureTicks", this, 2, 0, 20).addVisibleCondition(prediction).addExpandableParents(rotationsExpandable);
    private final ExpandableValue randomization = new ExpandableValue("Randomization", this).addExpandableParents(rotationsExpandable);
    private final BooleanValue positionRandomization = new BooleanValue("PositionRandomization", this, false).addExpandableParents(randomization);
    private final StringModeValue positionRandomizationMode = new StringModeValue("PositionRandomizationMode", this, "Heuristics", new String[]{"Heuristics", "HeuristicsLight", "Custom"}).addVisibleCondition(positionRandomization::getValue).addExpandableParents(randomization);
    private final RandomizedNumberValue<Float> randomizedXZ = new RandomizedNumberValue<>("RandomizedXZ", this, -0.5f, 0.5f, -1F, 1F, 1).addVisibleCondition(() -> positionRandomization.getValue() && positionRandomizationMode.is("Custom")).addExpandableParents(randomization);
    private final RandomizedNumberValue<Float> randomizedY = new RandomizedNumberValue<>("RandomizedY", this, -0.5f, 0.5f, -1F, 1F, 1).addVisibleCondition(() -> positionRandomization.getValue() && positionRandomizationMode.is("Custom")).addExpandableParents(randomization);
    private final RandomizationAlgorithmValue positionRandomizationAlgorithmValue = new RandomizationAlgorithmValue("PositionRandomizationAlgorithm", this).addVisibleCondition(positionRandomization::getValue).addExpandableParents(randomization);
    private final BooleanValue rotationRandomization = new BooleanValue("RotationRandomization", this, false).addExpandableParents(randomization);
    private final StringModeValue rotationRandomizationMode = new StringModeValue("RotationRandomizationMode", this, "Simple", new String[]{"Simple", "SimpleLegit", "Doubled", "Quadrupled", "Multipoints"}).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);
    private final NumberValue<Float> rotationRandomizationValue = new NumberValue<>("RotationRandomizationValue", this, 0.1f, 0f, 5f).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);
    private final RandomizationAlgorithmValue rotationRandomizationAlgorithmValue = new RandomizationAlgorithmValue("RotationRandomizationAlgorithm", this).addVisibleCondition(rotationRandomization::getValue).addExpandableParents(randomization);;

    private final ExpandableValue clicking = new ExpandableValue("Clicking", this);
    public final NumberValue<Float> attackRange = new NumberValue<>("AttackRange", this, 3f, 1f, 6f).addExpandableParents(clicking);
    private final NumberValue<Float> troughWallsAttackRange = new NumberValue<Float>("ThroughWallsAttackRange", this, 3f, 1f, 6f).addVisibleCondition(troughWalls::getValue).addExpandableParents(clicking);
    private final StringModeValue rangeCalculation = new StringModeValue("RangeCalc", this, "HeadToBody", new String[]{"HeadToBody", "FeetToBody", "BlockToBlock"}).addExpandableParents(clicking);
    private final StringModeValue rangeMode = new StringModeValue("RangeMode", this, "Simple", new String[]{"Simple", "HazeRange", "Dynamic"}).addExpandableParents(clicking);
    private final NumberValue<Double> hazeRangeAdjustment = new NumberValue<Double>("HazeRangeAdjustment", this, 0.5d, 0d, 1d, 2).addVisibleCondition(() -> rangeMode.is("HazeRange")).addExpandableParents(clicking);
    private final NumberValue<Double> maxHazeRange = new NumberValue<Double>("MaxHazeRange", this, 1d, 0d, 3d, 1).addVisibleCondition(() -> rangeMode.is("HazeRange")).addExpandableParents(clicking);
    private final RandomizedNumberValue<Float> onGroundIncrease = new RandomizedNumberValue<>("OnGroundIncrease", this, 0f, 0f, 0f, 3f, 0).addVisibleCondition(rangeMode, "Dynamic").addExpandableParents(clicking);
    private final RandomizedNumberValue<Float> offGroundIncrease = new RandomizedNumberValue<>("OffGroundIncrease", this, 0f, 0f, 0f, 3f, 0).addVisibleCondition(rangeMode, "Dynamic").addExpandableParents(clicking);
    private final RandomizedNumberValue<Float> playerHurtIncrease = new RandomizedNumberValue<>("PlayerHurtIncrease", this, 0f, 0f, 0f, 3f, 0).addVisibleCondition(rangeMode, "Dynamic").addExpandableParents(clicking);
    private final RandomizedNumberValue<Float> targetHurtIncrease = new RandomizedNumberValue<>("TargetHurtIncrease", this, 0f, 0f, 0f, 3f, 0).addVisibleCondition(rangeMode, "Dynamic").addExpandableParents(clicking);
    private final BooleanValue swingOnScanRange = new BooleanValue("SwingOnScanRange", this, false).addExpandableParents(clicking);
    private final MultiSelectNumberValue hurtTime = new MultiSelectNumberValue("HurtTime", this, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, 0, 10).addExpandableParents(clicking);
    private final BooleanValue perfectHit = new BooleanValue("PerfectHit", this, false).addExpandableParents(clicking);
    private final NumberValue<Integer> perfectHitPreciseness = new NumberValue<>("PerfectHitPreciseness", this, 2, 0, 5).addExpandableParents(clicking).addVisibleCondition(perfectHit::getValue);
    private final BooleanValue rayCast = new BooleanValue("RayCast", this, true).addExpandableParents(clicking);
    private final BooleanValue customRayCast = new BooleanValue("CustomRayCast", this, false).addVisibleCondition(rayCast).addExpandableParents(clicking);
    private final BooleanValue ignoreYaw = new BooleanValue("IgnoreYaw", this, false).addVisibleCondition(rayCast).addVisibleCondition(customRayCast).addExpandableParents(clicking);
    private final BooleanValue ignorePitch = new BooleanValue("IgnorePitch", this, false).addVisibleCondition(rayCast).addVisibleCondition(customRayCast).addExpandableParents(clicking);
    private final BooleanValue forceRayCast = new BooleanValue("ForceTarget", this, false).addExpandableParents(clicking).addVisibleCondition(rayCast);
    private final BooleanValue ignoredRayCast = new BooleanValue("IgnoreRayCastEntity", this, false).addExpandableParents(clicking).addVisibleCondition(rayCast).addVisibleCondition(forceRayCast, false);
    private final BooleanValue invalidRayCast = new BooleanValue("InvalidRayCastEntity", this, true).addExpandableParents(clicking).addVisibleCondition(rayCast).addVisibleCondition(ignoredRayCast, false).addVisibleCondition(forceRayCast, false);

    private final ExpandableValue cpsCalc = new ExpandableValue("CPSCalculation", this).addExpandableParents(clicking);
    private final BooleanValue startCPS = new BooleanValue("StartCPS", this, true).addExpandableParents(cpsCalc);
    private final NumberValue<Integer> startHits = new NumberValue<>("StartHits", this, 1, 0, 20).addVisibleCondition(startCPS).addExpandableParents(cpsCalc);
    private final BooleanValue maximumStartCPS = new BooleanValue("MaximumStartCPS", this, false).addVisibleCondition(startCPS).addExpandableParents(cpsCalc);
    private final RandomizedNumberValue<Integer> startingCPS = new RandomizedNumberValue<>("StartingCPS", this, 8, 11, 1, 20, 0).addVisibleCondition(startCPS).addVisibleCondition(maximumStartCPS, false).addExpandableParents(cpsCalc);
    private final BooleanValue maximumCPS = new BooleanValue("MaximumCPS", this, false).addExpandableParents(cpsCalc);
    private final RandomizedNumberValue<Integer> cps = new RandomizedNumberValue<>("CPS", this, 8, 11, 1, 20, 0).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc);
    private final BooleanValue spikes = new BooleanValue("Spikes", this, false).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc);
    private final RandomizedNumberValue<Integer> cpsSpike = new RandomizedNumberValue<>("CPSSpike", this, 2, 3, 0, 10, 0).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(spikes);
    private final MultiSelectNumberValue spikeTicks = new MultiSelectNumberValue("SpikeTicks", this, new int[] {0}, 0, 20).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(spikes);
    private final MultiSelectNumberValue spikeHurtTime = new MultiSelectNumberValue("SpikeHurtTime", this, new int[] {0, 1, 2}, 0, 10).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(spikes);
    private final BooleanValue drops = new BooleanValue("Drops", this, false).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc);
    private final RandomizedNumberValue<Integer> cpsDrop = new RandomizedNumberValue<>("CPSDrop", this, 2, 3, 0, 10, 0).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(drops);
    private final MultiSelectNumberValue dropTicks = new MultiSelectNumberValue("DropTicks", this, new int[] {0}, 0, 20).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(drops);
    private final MultiSelectNumberValue dropHurtTime = new MultiSelectNumberValue("DropHurtTime", this, new int[] {10, 9}, 0, 10).addVisibleCondition(maximumCPS, false).addExpandableParents(cpsCalc).addVisibleCondition(drops);
    private final NumberValue<Integer> cpsLimit = new NumberValue<>("CPSLimit", this, 15, 1, 20).addExpandableParents(cpsCalc);
    private final BooleanValue calculateAfterHit = new BooleanValue("CalculateAfterHit", this, true).addExpandableParents(cpsCalc);

    // Targeting
    public EntityLivingBase target;
    public final List<Entity> targets = new ArrayList<>();

    private PredictedPlayer predictedPlayer;

    // Rotations
    private final MSTimer simpleLegitTimer = new MSTimer();
    private boolean simpleLegitUp = true;
    private long simpleLegitDelay = 100;

    // Range
    private double curHazeRange = 0D;
    private long lastAttack = 0L;

    // Clicking
    private final MSTimer clickTimer = new MSTimer();
    private long attackDelay = 0;
    private int clicked = 0;

    @Listen
    public final void onMouseOver(MouseOverEvent mouseOverEvent) {
        if(target != null) {
            mouseOverEvent.setRange(attackRange.getValue());
            mouseOverEvent.setRangeCheck(false);
        }
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        switch (updateEvent.getStage()) {
            case PRE -> {
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

                    Object[] listOfTargets;

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
                                    return range <= (!mc.thePlayer.canEntityBeSeen(object) ? troughWallsAttackRange.getValue() : attackRange.getValue()) ? 0 : 1;
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
                                    return range <= (!mc.thePlayer.canEntityBeSeen(object) ? troughWallsAttackRange.getValue() : attackRange.getValue()) ? 0 : 1;
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
                                    return range <= (!mc.thePlayer.canEntityBeSeen(object) ? troughWallsAttackRange.getValue() : attackRange.getValue()) ? 0 : 1;
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
                                    return range <= (!mc.thePlayer.canEntityBeSeen(object) ? troughWallsAttackRange.getValue() : attackRange.getValue()) ? 0 : 1;
                                })))
                                .toArray();
                        default -> null;
                    };

                    if (!curTargetValid) {
                        target = null;
                    }

                    if(listOfTargets != null) {
                        target = listOfTargets.length == 0 ? null : (EntityLivingBase) listOfTargets[0];
                        predictedPlayer = new PredictedPlayer(target);
                    }
                }
            }
            case MID -> {
                if(target == null)
                    return;

                if (target.hurtTime == 10 && curHazeRange < maxHazeRange.getValue()) {
                    curHazeRange += hazeRangeAdjustment.getValue();
                    lastAttack = System.currentTimeMillis() / 1000;
                }

                if (System.currentTimeMillis() / 1000 - lastAttack >= 2) {
                    curHazeRange = 0;
                }
            }
        }
    }

    @Listen
    public void onBackground(BackgroundRunEvent backgroundRunEvent) {
        if(target != null) {
            double range = getRange(target);
            boolean behindAWall = !mc.thePlayer.canEntityBeSeen(target);
            double attackRangeValue = behindAWall ? (troughWalls.getValue() ? troughWallsAttackRange.getValue() : Double.MAX_VALUE) : attackRange.getValue();
            double scanRangeValue = behindAWall ? (troughWalls.getValue() ? troughWallsScanRange.getValue() : Double.MAX_VALUE) : scanRange.getValue();

            attackRangeValue += switch (rangeCalculation.getValue()) {
                case "HazeRange" -> curHazeRange;
                case "Dynamic" -> {
                    double addition = 0;

                    if(getPlayer().onGround) {
                        addition += onGroundIncrease.getValue();
                    } else {
                        addition += offGroundIncrease.getValue();
                    }
                    if(getPlayer().hurtTime != 0) {
                        addition += playerHurtIncrease.getValue();
                    }
                    if(target != null && target.hurtTime != 0) {
                        addition += targetHurtIncrease.getValue();
                    }

                    yield addition;
                }
                default -> 0;
            };

            if(!calculateAfterHit.getValue())
                attackDelay = getCPS();

            if(swingOnScanRange.getValue() ? range <= scanRangeValue : range <= attackRangeValue) {
                if(target != null && clickTimer.hasReached(attackDelay) && ClickHandler.getClicks() < cpsLimit.getValue()) {
                    if(ClickHandler.incrementClick()) {
                        clicked++;

                        if(calculateAfterHit.getValue())
                            attackDelay = getCPS();

                        clickTimer.reset();
                    }
                }
            }
        } else {
            clicked = 0;
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        final RotationUtil rotationUtil = RotationUtil.getRotationUtil();

        if(target != null) {
            // Initial values
            final double eyeX = getPosX();
            final double eyeY = getPosY() + getPlayer().getEyeHeight();
            final double eyeZ = getPosZ();

            // Finding ideal aim vector
            Vec3 aimVector = rotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1f), target.getEntityBoundingBox());

            switch (this.aimVector.getValue()) {
                case "Smart" -> {
                    MovingObjectPosition rayCast = RayCastUtil.rayCast(rotationUtil.getRotation(aimVector), 1);
                    if(rayCast != null && rayCast.entityHit == null) {
                        for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25) {
                            for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
                                for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
                                    Vec3 tempVec = target.getPositionVector().add(new Vec3(
                                            (target.getEntityBoundingBox().maxX - target.getEntityBoundingBox().minX) * xPercent,
                                            (target.getEntityBoundingBox().maxY - target.getEntityBoundingBox().minY) * yPercent,
                                            (target.getEntityBoundingBox().maxZ - target.getEntityBoundingBox().minZ) * zPercent));
                                    float[] rotation = rotationUtil.getRotation(tempVec);
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
                                float[] rotation = rotationUtil.getRotation(tempVec);
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

            double entityX = aimVector.xCoord;
            double entityY = aimVector.yCoord;
            double entityZ = aimVector.zCoord;

            // Prediction
            if(prediction.getValue() && predictedPlayer != null) {
                if(predictedPlayer.entity() != target)
                    predictedPlayer = new PredictedPlayer(target);

                Vec3 distance = predictedPlayer.differenceBetweenFuture(futureTicks.getValue());
                entityX += distance.xCoord;
                entityY += distance.yCoord;
                entityZ += distance.zCoord;
            }

            // Randomizing entity position
            if(this.positionRandomization.getValue()) {
                switch (positionRandomizationMode.getValue()) {
                    case "Heuristics" -> {
                        entityX += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                        entityY += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                        entityZ += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().nextDouble() * 0.1;
                    }
                    case "HeuristicsLight" -> {
                        final float randomPitch = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(0.015, 0.018);
                        float randomizedPitch = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(0.010, randomPitch);
                        float randomizedYaw = (float) positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-0.1, -0.3);
                        entityX += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-randomizedYaw, randomizedYaw);
                        entityZ += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(-randomizedYaw, randomizedYaw);
                        entityY += positionRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(randomizedPitch, -0.01);
                    }
                    case "Custom" -> {
                        entityX += randomizedXZ.getValue();
                        entityY += randomizedY.getValue();
                        entityZ += randomizedXZ.getValue();
                    }
                }
            }

            // Rotation calculation
            double x = entityX - eyeX;
            double y = entityY - eyeY;
            double z = entityZ - eyeZ;

            double angle = MathHelper.sqrt_double(x * x + z * z);

            float yawAngle = (float) (MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F;
            float pitchAngle = (float) (-(MathHelper.func_181159_b(y, angle) * 180.0D / Math.PI));

            // Post-calculation rotation randomization
            if(this.rotationRandomization.getValue()) {
                switch (rotationRandomizationMode.getValue()) {
                    case "Simple" -> {
                        yawAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                        pitchAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                    }
                    case "SimpleLegit" -> {
                        if (simpleLegitUp) {
                            pitchAngle += (float) Math.abs(Math.random() * rotationRandomizationValue.getValue());
                        } else {
                            pitchAngle -= (float) Math.abs(Math.random() * rotationRandomizationValue.getValue());
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
                        yawAngle += rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(Math.min(random1, random2), Math.max(random1, random2));
                        pitchAngle += rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(Math.min(random3, random4), Math.max(random3, random4));
                    }
                    case "Doubled" -> {
                        yawAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                        pitchAngle += (float) (Math.random() * rotationRandomizationValue.getValue());

                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                            yawAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                            pitchAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                        }
                    }
                    case "Multipoints" -> {
                        pitchAngle += (float) rotationRandomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat(0, rotationRandomizationValue.getValue() * 4);
                        yawAngle += (float) (Math.random() * rotationRandomizationValue.getValue());
                    }
                }
            }

            float yaw = yawAngle,
                    pitch = pitchAngle;

            MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

            if(skipRotations.getValue() && movingObjectPosition != null) {
                boolean shouldSkip = (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY || movingObjectPosition.entityHit == target) || (skipIfNear.getValue() && getRange(target) <= nearDistance.getValue());
                if(shouldSkip) {
                    if(skipMode.is("Yaw") || skipMode.is("Both"))
                        yaw = PlayerHandler.yaw;
                    if(skipMode.is("Pitch") || skipMode.is("Both"))
                        pitch = PlayerHandler.pitch;
                }
            }

            // Now that we are done with initial calculation and randomization let's go to smoothing
            float maximumYawDelta = yawDelta.getValue();
            float maximumPitchDelta = pitchDelta.getValue();

            float deltaYaw = yaw - PlayerHandler.yaw;

            if(roundYawDelta.getValue()) {
                deltaYaw = MathHelper.wrapAngleTo180_float(deltaYaw);
            }

            float deltaPitch = pitch - PlayerHandler.pitch;

            final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);

            if(divideByFPS.getValue()) {
                deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta) / fps * 4;
                deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta) / fps * 4;
            } else {
                deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta);
                deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta);
            }

            if(animateRotations.getValue()) {
                yaw = (float) AnimationUtil.move(PlayerHandler.yaw + deltaYaw, PlayerHandler.yaw, AnimationUtil.delta, yawAnimationSpeed.getValue() / 10);
                pitch = (float) AnimationUtil.move(PlayerHandler.pitch + deltaPitch, PlayerHandler.pitch, AnimationUtil.delta, pitchAnimationSpeed.getValue() / 10);
            } else {
                yaw = PlayerHandler.yaw + deltaYaw;
                pitch = PlayerHandler.pitch + deltaPitch;
            }

            if(this.customMouseFix.getValue()) {
                float[] fixed = rotationUtil.applyMouseFix(yaw, pitch, this.customMouseSpeed.getValue());
                yaw = fixed[0];
                pitch = fixed[1];
            }

            if(this.mouseFix.getValue()) {
                float[] fixed = rotationUtil.applyMouseFix(yaw, pitch);
                yaw = fixed[0];
                pitch = fixed[1];
            }
            
            rotationEvent.setYaw(yaw);
            rotationEvent.setPitch(pitch);
        }
    }

    @Listen
    public void onClick(ClickEvent clickEvent) {
        final RotationUtil rotationUtil = RotationUtil.getRotationUtil();

        if(target != null) {
            clickEvent.setCancelVanillaClick(true);

            float[] neededRotations = RotationUtil.getRotationUtil().getRotation(rotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1f), target.getEntityBoundingBox()));

            Entity attackEntity = target;
            MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

            if(customRayCast.getValue()) {
                movingObjectPosition = RayCastUtil.rayCast(new float[] {ignoreYaw.getValue() ? neededRotations[0] : PlayerHandler.yaw, ignorePitch.getValue() ? neededRotations[1] : PlayerHandler.pitch}, attackRange.getValue());
            }

            if(this.perfectHit.getValue() && mc.thePlayer.hurtTime == 0 && target.hurtTime > (5 - this.perfectHitPreciseness.getValue())) {
                clickEvent.setCancelled(true);
                return;
            }

            if(!hurtTime.is(target.hurtTime)) {
                clickEvent.setCancelled(true);
                return;
            }

            if(rayCast.getValue()) {
                if(movingObjectPosition == null || movingObjectPosition.entityHit == null || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                    clickEvent.setCancelled(true);
                    return;
                }

                if(forceRayCast.getValue() && movingObjectPosition.entityHit != target) {
                    clickEvent.setCancelled(true);
                    return;
                }

                if(!ignoredRayCast.getValue()) {
                    attackEntity = invalidRayCast.getValue() || isValid(movingObjectPosition.entityHit) ? movingObjectPosition.entityHit : null;
                }
            }

            double range = getRange(attackEntity);
            boolean behindAWall = !mc.thePlayer.canEntityBeSeen(target);
            double attackRangeValue = behindAWall ? (troughWalls.getValue() ? troughWallsAttackRange.getValue() : Double.MAX_VALUE) : attackRange.getValue();

            mc.thePlayer.swingItem();

            if(range <= attackRangeValue)
                mc.playerController.attackEntity(mc.thePlayer, attackEntity);
        }
    }

    private long getCPS() {
        double additional = 0;

        if(spikes.getValue()) {
            if(spikeTicks.is(mc.thePlayer.ticksExisted % 20) || spikeHurtTime.is(target.hurtTime))
                additional += cpsSpike.getValue();
        }

        if(drops.getValue()) {
            if(dropTicks.is(mc.thePlayer.ticksExisted % 20) || dropHurtTime.is(target.hurtTime))
                additional -= cpsDrop.getValue();
        }

         return startCPS.getValue() && clicked <= startHits.getValue() ? (maximumStartCPS.getValue() ? 1 : startingCPS.getCPSValue(additional)) : (maximumCPS.getValue() ? 1 : cps.getCPSValue(additional));
    }

    public boolean isValid(Entity entity) {
        return isValid(entity, true);
    }

    public boolean isValid(Entity entity, boolean checkRange) {
        if (entity == null || entity == mc.thePlayer || !(entity instanceof EntityLivingBase) || ((EntityLivingBase) entity).deathTime != 0) {
            return false;
        }

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
        return switch (rangeMode.getValue()) {
            default -> CombatUtil.getCombatUtil().getRange(entity);
            case "FeetToBody" -> mc.thePlayer.getDistanceToEntity(entity);
            case "BlockToBlock" -> WorldUtil.getWorldUtil().calculateDistance(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ), new BlockPos(entity.posX, entity.posY + 1, entity.posZ));
        };
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
            float yaw = RotationUtil.getRotationUtil().getFovToTarget(
                    entity.posX, entity.posY, entity.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch
            )[0];
            return Math.abs(yaw);
        } else {
            return 1000.0;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
    }

    @Override
    public String getSuffix() {
        return targetMode.getValue();
    }
}
