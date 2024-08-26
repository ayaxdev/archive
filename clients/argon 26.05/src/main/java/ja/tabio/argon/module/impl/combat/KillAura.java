package ja.tabio.argon.module.impl.combat;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.click.ClickMethod;
import ja.tabio.argon.component.click.impl.CPS;
import ja.tabio.argon.component.click.impl.Cooldown;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.PlayerTickEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.extension.impl.ConditionalSmoothingExtension;
import ja.tabio.argon.module.extension.impl.RandomNumberExtension;
import ja.tabio.argon.processor.impl.click.ClickProcessor;
import ja.tabio.argon.processor.impl.click.impl.Clicker;
import ja.tabio.argon.processor.impl.rotation.RotationProcessor;
import ja.tabio.argon.processor.impl.rotation.interfaces.RotationModifier;
import ja.tabio.argon.processor.impl.rotation.interfaces.Rotator;
import ja.tabio.argon.processor.impl.rotation.modifier.ConditionalSmoothingModifier;
import ja.tabio.argon.processor.impl.rotation.modifier.LinearSmoothingModifier;
import ja.tabio.argon.processor.impl.rotation.modifier.RelativeSmoothingModifier;
import ja.tabio.argon.processor.impl.rotation.modifier.SensitivityRotationModifier;
import ja.tabio.argon.processor.impl.rotation.strafe.StrafeCorrector;
import ja.tabio.argon.processor.impl.rotation.strafe.StrafeMode;
import ja.tabio.argon.processor.impl.target.TargetLookupProcessor;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.jvm.ObjectUtils;
import ja.tabio.argon.utils.player.MovementUtils;
import ja.tabio.argon.utils.timer.TimerUtils;
import ja.tabio.argon.utils.world.EntityUtils;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@RegisterModule
public class KillAura extends Module implements Rotator, Clicker {

    public TargetLookupProcessor.StoredEntity target = null;

    public final ModeSetting targetMode = new ModeSetting("TargetMode", "Priority", "Priority", "Single", "Switch");
    public final NumberSetting switchDelay = new NumberSetting("SwitchDelay", 300L, 0L, 1000L, 0)
            .visibility(targetMode, "Switch");
    public final NumberSetting targetRange = new NumberSetting("TargetRange", 4, 0, 7, 1);
    public final BooleanSetting players = new BooleanSetting("Players", true);
    public final BooleanSetting monsters = new BooleanSetting("Monsters", true);
    public final BooleanSetting passives = new BooleanSetting("Passives", false);
    public final BooleanSetting others = new BooleanSetting("Others", false);

    public final ModeSetting sortingMode = new ModeSetting("Sorting", "Distance",
             "Distance", "HealthArmorHybrid", "Health", "Armor");

    public final BooleanSetting lockView = new BooleanSetting("LockView", false);

    public final BooleanSetting fixSensitivity = new BooleanSetting("FixSensitivity", false);
    public final ModeSetting sensitivityMode = new ModeSetting("SensitivityMode", "Mode", "Real",
            SensitivityRotationModifier.SensitivityMode.values()).visibility(fixSensitivity);

    public final BooleanSetting smoothRotations = new BooleanSetting("SmoothRotations", false);
    public final ModeSetting smoothMode = new ModeSetting("SmoothMode", "Mode", "Relative",
            new String[]{"Relative", "Linear", "Conditional"}).visibility(smoothRotations);
    public final RandomNumberExtension turnSpeed = new RandomNumberExtension("TurnSpeed", this,
            100, 130, 0, 180, 1).visibilityN(smoothMode, "Conditional").visibility(smoothRotations);
    public final ConditionalSmoothingExtension conditionalSmoothingExtension = new ConditionalSmoothingExtension("ConditionalSmoothing",  this)
            .visibility(smoothMode, "Conditional").visibility(smoothRotations);

    public final BooleanSetting raycast = new BooleanSetting("RayCast", false);
    public final BooleanSetting raycastIgnored = new BooleanSetting("RayCastIgnored", false)
            .visibility(raycast);
    public final BooleanSetting perfectHit = new BooleanSetting("PerfectHit", false);
    public final NumberSetting perfectHitPrecision = new NumberSetting("PerfectHitPrecision", "Precision", 4, 0, 5, 0)
            .visibility(perfectHit);

    public final ModeSetting clickDelay = new ModeSetting("ClickDelay", "CPS", "CPS", "Cooldown");
    public final RandomNumberExtension targetClicks = new RandomNumberExtension("TargetClicks", this, 9, 12, 0, 20, 1)
            .visibility(clickDelay, "CPS");

    public final ModeSetting clickMethod = new ModeSetting("ClickMethod", "Legit", "Legit", "Direct");

    public final BooleanSetting strafeCorrection = new BooleanSetting("StrafeCorrection", false);
    public final ModeSetting strafeCorrectionMode = new ModeSetting("StrafeCorrectionMode", "Mode", "Simple", StrafeMode.values());

    private final ClickMethod cpsMethod = new CPS(),
            cooldownMethod = new Cooldown();

    private int switchIndex = 0;
    private long lastSwitchTime = 0L;

    public KillAura() {
        super(ModuleParams.builder()
                .name("KillAura")
                .category(ModuleCategory.COMBAT)
                .build());

        Argon.getInstance().processorManager.getByClass(RotationProcessor.class).add(this);
        Argon.getInstance().processorManager.getByClass(ClickProcessor.class).add(this);
    }

    @EventHandler
    public final void onPlayerTick(PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.stage != Stage.PRE || playerTickEvent.type != PlayerType.SERVER)
            return;

        final List<TargetLookupProcessor.StoredEntity> entities = new ArrayList<>(Argon.getInstance().processorManager
                .getByClass(TargetLookupProcessor.class).getInReach(targetRange.getValue()));

        entities.removeIf(entity -> switch (entity.entityType()) {
            case PLAYER -> !players.getValue();
            case PASSIVE -> !passives.getValue();
            case MONSTER -> !monsters.getValue();
            case OTHER -> !others.getValue();
        });

        TargetLookupProcessor.StoredEntity currentTargetIfValid = null;

        if (this.target != null) {
            for (TargetLookupProcessor.StoredEntity storedEntity : entities) {
                if (storedEntity.entity() == target.entity()) {
                    currentTargetIfValid = storedEntity;
                    break;
                }
            }
        }

        if (requiresTargetReload(currentTargetIfValid != null)) {
            if (currentTargetIfValid != null) {
                target = currentTargetIfValid;
            }
        }

        final Comparator<TargetLookupProcessor.StoredEntity> comparator = switch (sortingMode.getValue()) {
            default -> Comparator.comparing(TargetLookupProcessor.StoredEntity::distance);
            case "Health" -> Comparator.comparing(storedEntity -> storedEntity.entity().getHealth());
            case "Armor" -> Comparator.comparing(storedEntity -> storedEntity.entity().getArmor());
            case "HealthAndArmor" -> Comparator.comparing(storedEntity -> EntityUtils.getEffectiveHealth(storedEntity.entity()));
        };

        entities.sort(comparator);

        if (!entities.isEmpty()) {
            if (this.targetMode.is("Switch")) {
                if (++switchIndex >= entities.size())
                    switchIndex = 0;

                lastSwitchTime = System.currentTimeMillis();

                target = entities.get(switchIndex);
            } else {
                this.target = entities.getFirst();
            }
        } else
            target = null;
    }

    private boolean requiresTargetReload(boolean currentTargetValued) {
        if (!currentTargetValued)
            return true;

        return switch (targetMode.getValue()) {
            case "Single" -> false;
            case "Switch" -> TimerUtils.hasTimeElapsed(lastSwitchTime, switchDelay.getValue().longValue());
            default -> true;
        };
    }

    @Override
    public float[] rotate(float[] current, boolean tick, float tickDelta) {
        assert mc.player != null;

        if (target == null) {
            return new float[] {mc.player.getYaw(tickDelta), mc.player.getPitch(tickDelta)};
        }

        return MovementUtils.Math.getAngle(this.target.closest(), tickDelta);
    }

    @Override
    public boolean canRotate() {
        return isEnabled() && target != null;
    }

    @Override
    public int getPriorityRotations() {
        return 0;
    }

    private final TreeMap<Integer, RotationModifier> modifierTreeMap = new TreeMap<>();

    @Override
    public TreeMap<Integer, RotationModifier> getModifiers() {
        if (modifierTreeMap.isEmpty()) {
            modifierTreeMap.put(0, new LinearSmoothingModifier() {
                @Override
                protected float getTurnSpeed() {
                    return turnSpeed.getValue();
                }

                @Override
                public boolean isEnabled() {
                    return smoothRotations.getValue() && smoothMode.is("Linear");
                }
            });
            modifierTreeMap.put(1, new RelativeSmoothingModifier() {
                @Override
                protected float getTurnSpeed() {
                    return turnSpeed.getValue();
                }

                @Override
                public boolean isEnabled() {
                    return smoothRotations.getValue() && smoothMode.is("Relative");
                }
            });
            modifierTreeMap.put(2, new ConditionalSmoothingModifier() {
                @Override
                protected float getCoefDistance() {
                    return conditionalSmoothingExtension.getCoefDistance();
                }

                @Override
                protected float[] getCoefDiff() {
                    return conditionalSmoothingExtension.getCoefDiff();
                }

                @Override
                protected float[] getCoefCrosshair() {
                    return conditionalSmoothingExtension.getCoefCrosshair();
                }

                @Override
                protected float[] getIntercept() {
                    return conditionalSmoothingExtension.getIntercept();
                }

                @Override
                protected float[] getMinimumTurnSpeed() {
                    return conditionalSmoothingExtension.getMinimumTurnSpeed();
                }

                @Override
                public boolean isEnabled() {
                    return smoothRotations.getValue() && smoothMode.is("Conditional");
                }

            });
            modifierTreeMap.put(3, new SensitivityRotationModifier() {
                @Override
                protected SensitivityMode getSensitivityMode() {
                    return ObjectUtils.getEnum(SensitivityMode.class, sensitivityMode.getValue());
                }

                @Override
                public boolean isEnabled() {
                    return fixSensitivity.getValue();
                }
            });
        }

        return modifierTreeMap;
    }

    @Override
    public int getFlags() {
        int bitFlag = 0;

        if (lockView.getValue()) {
            bitFlag = bitFlag | RotationProcessor.RotatorFlags.NON_SILENT;
        }

        return bitFlag;
    }

    private StrafeCorrector strafeCorrector;

    @Override
    public StrafeCorrector getCorrector() {
        if (strafeCorrection.getValue()) {
            try {
                final StrafeMode strafeMode = ObjectUtils.getEnum(StrafeMode.class, strafeCorrectionMode.getValue());

                if (this.strafeCorrector == null || this.strafeCorrector.getClass() != strafeMode.corrector)
                    this.strafeCorrector = strafeMode.corrector.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to update strafe corrector");
            }

            return strafeCorrector;
        } else
            return null;
    }

    @Override
    public void runClick(ClickProcessor callback) {
        if (!isEnabled())
            return;

        if (!Minecraft.inGame())
            return;

        assert mc.player != null;

        if (target == null)
            return;

        if (raycast.getValue()) {
            if (mc.crosshairTarget == null)
                return;

            if (mc.crosshairTarget.getType() != HitResult.Type.ENTITY)
                return;

            final EntityHitResult entityHitResult = ((EntityHitResult) mc.crosshairTarget);

            if (raycastIgnored.getValue() && entityHitResult.getEntity() != target.entity())
                return;
        }

        if (perfectHit.getValue()) {
            final int hurtTime = target.entity().hurtTime;
            final int maximumHurtTime = (int) (perfectHitPrecision.maximum - perfectHitPrecision.getValue());

            if (hurtTime > maximumHurtTime)
                return;
        }

        final ClickMethod selected = clickDelay.is("Cooldown") ? cooldownMethod : cpsMethod;

        final double target = this.targetClicks.getValue();

        int clicks = selected.getClicks(target);

        if (clicks > 0) {
            switch (clickMethod.getValue()) {
                case "Legit" -> callback.left();
                case "Direct" -> callback.attackEntity(this.target.entity());
            }

            selected.update(target);
        }
    }
}
