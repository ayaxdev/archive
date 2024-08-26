package lord.daniel.alexander.module.impl.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.*;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.module.impl.combat.KillAuraModule;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.entity.EntityUtil;
import lord.daniel.alexander.util.math.time.TimeHelper;
import lord.daniel.alexander.util.player.MoveUtil;
import lord.daniel.alexander.util.player.PlayerUtil;
import lord.daniel.alexander.util.rotation.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "ScaffoldWalk", categories = {EnumModuleType.WORLD, EnumModuleType.MOVEMENT})
public class ScaffoldWalkModule extends AbstractModule {

    private final ExpandableValue global = new ExpandableValue("Global", this);
    private final NumberValue<Long> startingTime = new NumberValue<>("StartingDelay", this, 200L, 0L, 2000L).addExpandableParents(global);
    private final BooleanValue disableIfNotHoldingBlocks = new BooleanValue("DisableIfNotHoldingBlocks", this, true).addExpandableParents(global);
    private final BooleanValue disableIfKillAura = new BooleanValue("DisableIfKillAuraHasTargets", this, true).addExpandableParents(global);
    private final BooleanValue activeOnlyIfNeeded = new BooleanValue("ActiveOnlyIfNeeded", this, false).addExpandableParents(global);
    private final BooleanValue ignoreKillAuraOnFallDistance = new BooleanValue("IgnoreKillAuraOnFallDistance", this, true).addVisibleCondition(disableIfKillAura::getValue).addExpandableParents(global);
    private final NumberValue<Integer> fallDistance = new NumberValue<>("FallDistance", this, 5, 0, 15).addVisibleCondition(() -> disableIfKillAura.getValue() && ignoreKillAuraOnFallDistance.getValue()).addExpandableParents(global);
    private final ExpandableValue placing = new ExpandableValue("Placing", this);
    private final StringModeValue hitVector = new StringModeValue("HitVector", this, "Real", new String[]{"Real", "Zero", "BlockPos", "FakeDirection", "FakeStrict", "FakeLegit"}).addExpandableParents(placing);
    private final ExpandableValue rotationExpandable = new ExpandableValue("Rotations", this);
    private final BooleanValue lockView = new BooleanValue("LockView", this, false).addExpandableParents(rotationExpandable);
    private final StringModeValue lockViewMode = new StringModeValue("LockViewMode", this, "Both", new String[]{"Both", "Yaw", "Pitch"}).addVisibleCondition(lockView::getValue).addExpandableParents(rotationExpandable);
    private final StringModeValue rotationMode = new StringModeValue("RotationMode", this, "OnlyNeeded", new String[]{"ReversedFixed", "Reversed"}).addExpandableParents(rotationExpandable);
    private final ExpandableValue startingRotationDeltaExpandable = new ExpandableValue("StartingRotationDelta", this).addExpandableParents(rotationExpandable);
    private final RandomizedNumberValue<Float> startYawDelta = new RandomizedNumberValue<>("MaximumStartingYawDelta", this, 5.5f, 6.5f, 0f, 180f, 1).addExpandableParents(startingRotationDeltaExpandable);
    private final RandomizedNumberValue<Float> startPitchDelta = new RandomizedNumberValue<>("MaximumStartingPitchDelta", this, 5.5f, 6.5f, 0f, 180f, 1).addExpandableParents(startingRotationDeltaExpandable);
    private final ExpandableValue rotationDeltaExpandable = new ExpandableValue("RotationDelta", this).addExpandableParents(rotationExpandable);
    private final RandomizedNumberValue<Float> yawDelta = new RandomizedNumberValue<>("MaximumYawDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationDeltaExpandable);
    private final RandomizedNumberValue<Float> pitchDelta = new RandomizedNumberValue<>("MaximumPitchDelta", this, 40f, 40f, 0f, 180f, 1).addExpandableParents(rotationDeltaExpandable);
    private final BooleanValue roundYawDelta = new BooleanValue("RoundYawDelta", this, true).addExpandableParents(rotationExpandable);
    private final BooleanValue divideByFPS = new BooleanValue("DivideDeltaByFPS", this, false).addExpandableParents(rotationExpandable);
    private final BooleanValue resetRotations = new BooleanValue("ResetRotations", this, true).addExpandableParents(rotationExpandable);
    private final StringModeValue resetMode = new StringModeValue("ResetMode", this, "Silent", new String[]{"Silent", "Visible"}).addVisibleCondition(resetRotations::getValue).addExpandableParents(rotationExpandable);
    private final ExpandableValue items = new ExpandableValue("Items", this);
    private final BooleanValue switchItems = new BooleanValue("SwitchItems", this, true).addExpandableParents(items);
    private final ExpandableValue blockSearch = new ExpandableValue("Search", this);
    private final BooleanValue sameY = new BooleanValue("SameY", this, false).addExpandableParents(blockSearch);
    private final ExpandableValue movement = new ExpandableValue("Movement", this);
    private final NumberValue<Float> motionMultiplier = new NumberValue<>("MotionMultiplier", this, 1F, 0.1F, 3F).addExpandableParents(movement);
    private final BooleanValue eagle = new BooleanValue("Eagle", this, false).addExpandableParents(movement);
    private final BooleanValue eagleSilentValue = new BooleanValue("EagleSilent", this, false).addVisibleCondition(eagle::getValue).addExpandableParents(movement);
    private final NumberValue<Integer> blocksToEagleValue = new NumberValue<>("BlocksToEagle", this, 0, 0, 10).addVisibleCondition(eagle::getValue).addExpandableParents(movement);
    private final BooleanValue reverseMovement = new BooleanValue("ReverseMovement", this, false).addExpandableParents(movement);
    private final StringModeValue reverseMovementMode = new StringModeValue("ReverseMovementMode", this, "Both", new String[]{"Both", "ForwardsBackwards", "LeftRight"}).addVisibleCondition(reverseMovement::getValue).addExpandableParents(movement);
    private final BooleanValue additionalZitter = new BooleanValue("AdditionalZitter", this, false).addExpandableParents(movement);
    private final BooleanValue zitterOnlyOnGround = new BooleanValue("ZitterOnlyOnGround", this, true).addVisibleCondition(additionalZitter::getValue).addExpandableParents(movement);
    private final StringModeValue zitterMode = new StringModeValue("ZitterMode", this, "Normal", new String[]{"Normal", "Teleport"}).addVisibleCondition(additionalZitter::getValue).addExpandableParents(movement);
    private final NumberValue<Integer> zitterModulo = new NumberValue<>("ZitterTicksModulo", this, 4, 0, 40).addVisibleCondition(additionalZitter::getValue).addVisibleCondition(() -> zitterMode.is("Normal")).addExpandableParents(movement);
    private final NumberValue<Float> zitterStrength = new NumberValue<>("ZitterTpStrength", this, 0.072F, 0.05F, 0.2F).addVisibleCondition(additionalZitter::getValue).addVisibleCondition(() -> zitterMode.is("Teleport")).addExpandableParents(movement);
    private final BooleanValue stopMovementIfDisabled = new BooleanValue("StopMovementIfDisabled", this, true).addExpandableParents(movement);
    private final ExpandableValue towerSettings = new ExpandableValue("Tower", this).addExpandableParents(movement);
    private final StringModeValue towerMode = new StringModeValue("TowerMode", this, "None", new String[]{"None", "Watchdog", "Custom"}).addExpandableParents(towerSettings);
    private final NumberValue<Float> towerTimer = new NumberValue<>("TowerTimer", this, 1f, 1F, 5F).addExpandableParents(towerSettings);
    private final ExpandableValue extrasExpandable = new ExpandableValue("Extras", this);
    private final BooleanValue vulcanLimitBypass = new BooleanValue("VulcanLimitBypass", this, false).addExpandableParents(extrasExpandable);

    private static final List<Block> INVALID_BLOCKS = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
            Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
            Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
            Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
            Blocks.yellow_flower, Blocks.bed, Blocks.ladder, Blocks.waterlily, Blocks.double_stone_slab, Blocks.stone_slab,
            Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_slab2,
            Blocks.double_stone_slab2, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser,
            Blocks.command_block, Blocks.web, Blocks.soul_sand);

    // Global
    private BlockPos blockPos;
    private boolean starting;
    private final TimeHelper startingTimeHelper = new TimeHelper();
    private KillAuraModule killAuraModule;
    private int placedBlocks = 0;

    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;

    // Switching
    private int lastItem = -1;

    // Rotations
    private float curYaw, curPitch;
    public boolean rotations = false;

    // Zitter
    private boolean zitterDirection;
    private final TimeHelper zitterTimer = new TimeHelper();

    // Calc
    private double[] lastPos = new double[3];

    // watchdog tower
    private boolean towering = false;
    private boolean spoofGround = false;

    // vulcan sneak bypass
    private int ticksSinceLastPlace = 0;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(rotationMode.getValue());
    };

    @EventLink
    public final Listener<RotationEvent> rotationEventListener = rotationEvent -> {
        Disable disable = shouldDisable();

        if (disable == Disable.ALL || disable == Disable.ONLY_ROTATION) {
            return;
        }

        blockPos = getAimBlockPos();

        if (blockPos != null) {
            float[] rotations = this.getRotations();
            float yaw = rotations[0],
                    pitch = rotations[1];

            float maximumYawDelta, maximumPitchDelta;
            if (starting) {
                maximumYawDelta = this.startYawDelta.getValue();
                maximumPitchDelta = this.startPitchDelta.getValue();
            } else {
                maximumYawDelta = this.yawDelta.getValue();
                maximumPitchDelta = this.pitchDelta.getValue();
            }

            float deltaYaw = roundYawDelta.getValue() ? (((yaw - PlayerHandler.yaw) + 540) % 360) - 180 : yaw - PlayerHandler.yaw;
            float deltaPitch = pitch - PlayerHandler.pitch;

            final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);

            if (divideByFPS.getValue()) {
                deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta) / fps * 4;
                deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta) / fps * 4;
            } else {
                deltaYaw = MathHelper.clamp_float(deltaYaw, -maximumYawDelta, maximumYawDelta);
                deltaPitch = MathHelper.clamp_float(deltaPitch, -maximumPitchDelta, maximumPitchDelta);
            }

            curYaw = PlayerHandler.yaw + deltaYaw;
            curPitch = PlayerHandler.pitch + deltaPitch;

            rotationEvent.setYaw(curYaw);
            rotationEvent.setPitch(curPitch);

            this.rotations = true;
        }
    };

    @EventLink
    public final Listener<EntityRendererEvent> rendererEventListener = entityRendererEvent -> {
        Disable disable = shouldDisable();
        if (blockPos == null || disable == Disable.ALL || disable == Disable.ONLY_ROTATION) {
            if (this.rotations && resetRotations.getValue()) {
                RotationUtil.resetRotations(PlayerHandler.yaw, PlayerHandler.pitch, resetMode.is("Silent"));
                rotations = false;
            }
            return;
        }

        if (this.lockView.getValue()) {
            switch (this.lockViewMode.getValue()) {
                case "Both" -> {
                    mc.thePlayer.rotationYaw = curYaw;
                    mc.thePlayer.rotationPitch = curPitch;
                }
                case "Yaw" -> mc.thePlayer.rotationYaw = curYaw;
                case "Pitch" -> mc.thePlayer.rotationPitch = curPitch;
            }
        }
    };

    private float[] getRotations() {
        float reverseYaw = mc.thePlayer.rotationYaw + (Keyboard.isKeyDown(reverseMovement.getValue() && (reverseMovementMode.is("Both") || reverseMovementMode.is("ForwardsBackwards")) ? mc.gameSettings.keyBindForward.getKeyCode() : mc.gameSettings.keyBindBack.getKeyCode()) ? 0 : 180);

        if (this.rotationMode.getValue().equals("Reversed")) {
            return new float[]{reverseYaw, 80.34f};
        }
        final float[] angles = {PlayerHandler.yaw, PlayerHandler.pitch};
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        if (this.starting) {
            angles[1] = 80.34f;
            angles[0] = reverseYaw;
        } else {
            angles[0] = reverseYaw;
            double x = mc.thePlayer.posX;
            double z = mc.thePlayer.posZ;
            final double add1 = 1.288;
            final double add2 = 0.288;

            if (!PlayerUtil.canBuildForward()) {
                x += mc.thePlayer.posX - this.lastPos[0];
                z += mc.thePlayer.posZ - this.lastPos[2];
            }

            this.lastPos = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
            final double maxX = this.blockPos.getX() + add1;
            final double minX = this.blockPos.getX() - add2;
            final double maxZ = this.blockPos.getZ() + add1;
            final double minZ = this.blockPos.getZ() - add2;

            if (x > maxX || x < minX || z > maxZ || z < minZ) {
                final List<MovingObjectPosition> hitBlockList = new ArrayList<>();
                final List<Float> pitchList = new ArrayList<>();

                for (float pitch = Math.max(PlayerHandler.pitch - 20.0f, -90.0f); pitch < Math.min(PlayerHandler.pitch + 20.0f, 90.0f); pitch += 0.05f) {
                    final float[] rotation = RotationUtil.applyMouseFix(reverseYaw, pitch);
                    final MovingObjectPosition hitBlock = mc.thePlayer.customRayTrace(4.5, 1.0f, reverseYaw, rotation[1]);

                    if (hitBlock.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                            && isOkBlock(hitBlock.getBlockPos())
                            && !hitBlockList.contains(hitBlock)
                            && hitBlock.getBlockPos().equalsBlockPos(this.blockPos)
                            && hitBlock.sideHit != EnumFacing.DOWN
                            && hitBlock.sideHit != EnumFacing.UP
                            && hitBlock.getBlockPos().getY() <= playerPos.getY()) {
                        hitBlockList.add(hitBlock);
                        pitchList.add(rotation[1]);
                    }
                }

                hitBlockList.sort(Comparator.comparingDouble(m -> mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
                MovingObjectPosition nearestBlock = null;

                if (!hitBlockList.isEmpty()) {
                    nearestBlock = hitBlockList.get(0);
                }

                if (nearestBlock != null) {
                    angles[0] = reverseYaw;
                    pitchList.sort(Comparator.comparingDouble(RotationUtil::getDistanceToLastPitch));

                    if (!pitchList.isEmpty()) {
                        angles[1] = pitchList.get(0);
                    }

                    return angles;
                }
            } else {
                angles[1] = PlayerHandler.pitch;
            }
        }

        return angles;
    }

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if(updateMotionEvent.getStage() == UpdateMotionEvent.Stage.MID) {
            ticksSinceLastPlace++;
        }
    };

    boolean stopped = false;

    @EventLink
    public final Listener<LivingUpdateEvent> livingUpdateEventListener = livingUpdateEvent -> {
        if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0 && mc.thePlayer.onGround) {
            starting = true;
            startingTimeHelper.reset();
        }
        if (startingTimeHelper.hasReached(startingTime.getValue())) {
            starting = false;
        }

        if (switchItems.getValue()) {
            if ((mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) || mc.thePlayer.getHeldItem() == null) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                    if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock && !INVALID_BLOCKS.contains(((ItemBlock) stack.getItem()).getBlock())) {
                        if (lastItem == -1) {
                            lastItem = mc.thePlayer.inventory.currentItem;
                        }
                        mc.thePlayer.inventory.currentItem = i;
                    }
                }
            }
        }

        Disable disable = shouldDisable();

        if (disable == Disable.ALL || disable == Disable.ONLY_MOVEMENT) {
            if (stopMovementIfDisabled.getValue()) {
                stopWalk();
                stopped = true;
            }
            return;
        } else {
            resumeWalk();
            stopped = false;
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                mc.timer.timerSpeed = towerTimer.getValue().intValue();
            } else {
                mc.timer.timerSpeed = 1F;
            }
        }

        if (reverseMovement.getValue()) {
            if (reverseMovementMode.is("Both") || reverseMovementMode.is("ForwardsBackwards")) {
                getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
                getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
            }
            if (reverseMovementMode.is("Both") || reverseMovementMode.is("LeftRight")) {
                getGameSettings().keyBindLeft.pressed = isKeyDown(getGameSettings().keyBindRight.getKeyCode());
                getGameSettings().keyBindRight.pressed = isKeyDown(getGameSettings().keyBindLeft.getKeyCode());
            }
        }

        mc.thePlayer.motionX *= motionMultiplier.getValue();
        mc.thePlayer.motionZ *= motionMultiplier.getValue();
    };

    @EventLink
    public final Listener<ClickingEvent> clickingEventListener = clickingEvent -> {
        Disable disable = shouldDisable();

        if (disable == Disable.ALL) {
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null || mc.objectMouseOver == null) {
            return;
        }

        if(!rotations)
            return;

        final MovingObjectPosition objectOver = mc.objectMouseOver;
        final EnumFacing enumFacing = mc.objectMouseOver.sideHit;
        final BlockPos blockpos = mc.objectMouseOver.getBlockPos();
        final ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

        if (objectOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
            return;
        }
        if (itemstack != null && !(itemstack.getItem() instanceof ItemBlock)) {
            return;
        }

        Vec3 hitVector = switch (this.hitVector.getValueAsString()) {
            case "Zero" -> new Vec3(0, 0, 0);
            case "BlockPos" -> new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            case "FakeDirection" -> {
                Vec3 eyesPos = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY + (double)this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
                Vec3 rotationVector = Entity.getVectorForRotation(PlayerHandler.yaw, PlayerHandler.pitch);
                yield eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
            }
            case "FakeStrict" -> {
                double x = (double)blockpos.getX() + 0.5;
                double y = (double)blockpos.getY() + 0.5;
                double z = (double)blockpos.getZ() + 0.5;
                if (enumFacing != EnumFacing.UP && enumFacing != EnumFacing.DOWN) {
                    y += 0.5;
                } else {
                    x += 0.3;
                    z += 0.3;
                }
                if (enumFacing == EnumFacing.WEST || enumFacing == EnumFacing.EAST) {
                    z += 0.15;
                }
                if (enumFacing == EnumFacing.SOUTH || enumFacing == EnumFacing.NORTH) {
                    x += 0.15;
                }
                yield new Vec3(x, y, z);
            }
            case "FakeLegit" -> {
                double x1 = (float)blockpos.getX() + 0.5f + 0.25f * (float)enumFacing.getDirectionVec().getX();
                double y1 = (float)blockpos.getY() + 0.5f + 0.25f * (float)enumFacing.getDirectionVec().getY();
                double z1 = (float)blockpos.getZ() + 0.5f + 0.25f * (float)enumFacing.getDirectionVec().getZ();
                yield new Vec3(x1, y1, z1);
            }
            default -> objectOver.hitVec;
        };

        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, objectOver.sideHit, hitVector)) {
            mc.thePlayer.swingItem();

            placedBlocks++;

            if(vulcanLimitBypass.getValue()){
                if(placedBlocks % 6 == 0){
                    sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    if(ticksSinceLastPlace > 0) {
                        sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
        if (itemstack != null && itemstack.stackSize == 0) {
            mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
        }

        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
    };

    @EventLink
    public final Listener<BlockPlaceEvent> blockPlaceEventListener = blockPlaceEvent -> {
        ticksSinceLastPlace = 0;
    };

    private int zitterTicks = 0;

    @EventLink
    public final Listener<MovementInputEvent> movementInputEventListener = movementInputEvent -> {
        if (additionalZitter.getValue() && (!zitterOnlyOnGround.getValue() || mc.thePlayer.onGround)) {
            if (zitterMode.is("Normal") && zitterTicks % zitterModulo.getValue() == 0) {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
                    movementInputEvent.setRight(false);

                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
                    movementInputEvent.setLeft(false);

                if (zitterTimer.hasReached(100)) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    movementInputEvent.setRight(true);
                } else {
                    movementInputEvent.setLeft(true);
                }
            }

            zitterTicks++;
        }
    };

    @EventLink
    public final Listener<SilentMoveEvent> silentMoveEventListener = silentMoveEvent -> {
        Disable disable = shouldDisable();

        if (disable == Disable.ALL || disable == Disable.ONLY_MOVEMENT) {
            return;
        }

        if (additionalZitter.getValue() && (!zitterOnlyOnGround.getValue() || mc.thePlayer.onGround)) {
            if (zitterMode.getValueAsString().equals("Teleport")) {
                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw + (zitterDirection ? 90D : -90D));
                mc.thePlayer.motionX -= Math.sin(yaw) * zitterStrength.getValue();
                mc.thePlayer.motionZ += Math.cos(yaw) * zitterStrength.getValue();
                zitterDirection = !zitterDirection;
            }
        }

        if (eagle.getValue()) {
            if (placedBlocksWithoutEagle >= blocksToEagleValue.getValue()) {
                final boolean shouldEagle = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX,
                        mc.thePlayer.posY - 1D, mc.thePlayer.posZ)).getBlock() == Blocks.air;

                if (eagleSilentValue.getValue()) {
                    if (eagleSneaking != shouldEagle) {
                        mc.getNetHandler().addToSendQueue(
                                new C0BPacketEntityAction(mc.thePlayer, shouldEagle ?
                                        C0BPacketEntityAction.Action.START_SNEAKING :
                                        C0BPacketEntityAction.Action.STOP_SNEAKING)
                        );
                    }

                    eagleSneaking = shouldEagle;
                } else
                    mc.gameSettings.keyBindSneak.pressed = shouldEagle;

                placedBlocksWithoutEagle = 0;
            } else
                placedBlocksWithoutEagle++;
        }

        switch (towerMode.getValueAsString()) {
            case "Watchdog" -> {
                double horizontal_boost = 20; // 23 max (u can prob use a bit more)
                double vertical_boost = 1.4; // 1.6 max but flags sometimes
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    double dist = Math.ceil((mc.thePlayer.posY % 1.0D) * 100.0D);
                    float angle = mc.thePlayer.rotationYaw * 0.017453292F;
                    boolean vertical = Keyboard.isKeyDown(Keyboard.KEY_B);
                    if (mc.thePlayer.motionX != 0 && mc.thePlayer.motionZ != 0) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionX -= MathHelper.sin(angle) * horizontal_boost / 100.0F;
                            mc.thePlayer.motionZ += MathHelper.cos(angle) * horizontal_boost / 100.0F;
                            mc.thePlayer.jump();
                        } else if (dist == 42) {
                            mc.thePlayer.motionY = 0.33;
                        } else if (dist == 75) {
                            mc.thePlayer.motionY = 1.0D - mc.thePlayer.posY % 1.0D;
                            spoofGround = true;
                        } else if (dist == 0) {
                            towering = true;
                            if (vertical) {
                                mc.thePlayer.motionX -= MathHelper.sin(angle) * (vertical_boost / 100);
                                mc.thePlayer.motionZ += MathHelper.cos(angle) * (vertical_boost / 100);
                                mc.thePlayer.jump();
                            } else {
                                mc.thePlayer.motionY = -0.0784000015258789D;
                            }
                        } else if (towering) {
                            towering = false;
                            MoveUtil.getMoveUtil().setSpeed(0.05);
                        }
                    }
                }
            }

        }

    };

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if (packetEvent.getPacket() instanceof C03PacketPlayer c03PacketPlayer) {
            if (spoofGround && towerMode.is("Watchdog")) {
                c03PacketPlayer.setOnGround(true);
                spoofGround = false;
            }
        }
    };

    @Override
    public void onEnable() {
        spoofGround = false;
        startingTimeHelper.reset();
        starting = true;
    }

    @Override
    public void onDisable() {
        if (this.lastItem != -1) {
            mc.thePlayer.inventory.currentItem = this.lastItem;
            this.lastItem = -1;
        }

        if (this.rotations && resetRotations.getValue()) {
            RotationUtil.resetRotations(PlayerHandler.yaw, PlayerHandler.pitch, resetMode.is("Silent"));
            rotations = false;
        }

        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            if (eagleSneaking)
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }

        mc.timer.timerSpeed = 1F;
    }

    private Disable shouldDisable() {
        if (killAuraModule == null) {
            killAuraModule = ModuleStorage.getModuleStorage().getByClass(KillAuraModule.class);
        }

        Disable current = Disable.NONE;

        if(this.activeOnlyIfNeeded.getValue() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX,
                mc.thePlayer.posY - 1D, mc.thePlayer.posZ)).getBlock() != Blocks.air)
            current = Disable.ONLY_ROTATION;

        if(this.disableIfNotHoldingBlocks.getValue() && (mc.thePlayer.getHeldItem() == null || (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))))
            current = Disable.ALL;

        if(this.disableIfKillAura.getValue() && (!this.ignoreKillAuraOnFallDistance.getValue() || !(mc.thePlayer.fallDistance > fallDistance.getValue())) && killAuraModule.isEnabled() && killAuraModule.target != null && EntityUtil.getRange(killAuraModule.target) <= killAuraModule.attackRange.getValue()) {
            current = Disable.ALL;
        }

        return current;
    }

    public enum Disable {
        ONLY_MOVEMENT, ONLY_ROTATION, ALL, NONE;
    }

    public static BlockPos getAimBlockPos() {
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);

        if ((mc.gameSettings.keyBindJump.isKeyDown() || !mc.thePlayer.onGround)
                && mc.thePlayer.moveForward == 0.0f
                && mc.thePlayer.moveStrafing == 0.0f
                && isOkBlock(playerPos.add(0, -1, 0))) {
            return playerPos.add(0, -1, 0);
        }

        BlockPos blockPos = null;
        final List<BlockPos> blockPosList = getBlockPos();

        if (!blockPosList.isEmpty()) {
            blockPosList.sort(Comparator.comparingDouble(ScaffoldWalkModule::getDistanceToBlockPos));
            blockPos = blockPosList.get(0);
        }

        return blockPos;
    }


    public static List<BlockPos> getBlockPos() {
        final BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
        final List<BlockPos> blockPosList = new ArrayList<>();

        for (int x = playerPos.getX() - 2; x <= playerPos.getX() + 2; ++x) {
            for (int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
                for (int z = playerPos.getZ() - 2; z <= playerPos.getZ() + 2; ++z) {
                    final BlockPos currentPos = new BlockPos(x, y, z);

                    if (isOkBlock(currentPos)) {
                        blockPosList.add(currentPos);
                    }
                }
            }
        }

        if (!blockPosList.isEmpty()) {
            blockPosList.sort(Comparator.comparingDouble(blockPos -> mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)));
        }

        return blockPosList;
    }

    public static double getDistanceToBlockPos(final BlockPos blockPos) {
        double distance = 1337.0;

        for (float x = (float) blockPos.getX(); x <= blockPos.getX() + 1; x += (float) 0.2) {
            for (float y = (float) blockPos.getY(); y <= blockPos.getY() + 1; y += (float) 0.2) {
                for (float z = (float) blockPos.getZ(); z <= blockPos.getZ() + 1; z += (float) 0.2) {
                    final double d0 = mc.thePlayer.getDistance(x, y, z);

                    if (d0 < distance) {
                        distance = d0;
                    }
                }
            }
        }

        return distance;
    }

    public static boolean isOkBlock(final BlockPos blockPos) {
        final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid)
                && !(block instanceof BlockAir)
                && !(block instanceof BlockChest)
                && !(block instanceof BlockFurnace);
    }

}
