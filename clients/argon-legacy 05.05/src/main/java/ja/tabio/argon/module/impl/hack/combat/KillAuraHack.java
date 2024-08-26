package ja.tabio.argon.module.impl.hack.combat;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.extension.AutoClickerExtension;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.PlayerRotationEvent;
import ja.tabio.argon.event.impl.PlayerUpdateEvent;
import ja.tabio.argon.event.impl.PreAttackEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.processor.impl.CombatProcessor;
import ja.tabio.argon.processor.impl.RotationProcessor;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.MultiSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.math.aabb.BBMathUtil;
import ja.tabio.argon.utils.player.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@ModuleData(name = "KillAura", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.COMBAT)
public class KillAuraHack extends Module {

    public final NumberSetting targetRange = new NumberSetting("TargetRange", 3, 0, 6, 1);
    public final MultiSetting targetReloadEvent = new MultiSetting("TargetReloadEvent", new String[]{"PreRotation"}, new String[]{"PreRotation", "PreAttack", "PreUpdate"});
    public final BooleanSetting switchTargets = new BooleanSetting("Switch", true);
    public final BooleanSetting players = new BooleanSetting("Players", true);
    public final BooleanSetting monsters = new BooleanSetting("Monsters", false);
    public final BooleanSetting passives = new BooleanSetting("Passives", false);
    public final BooleanSetting invisibles = new BooleanSetting("Invisibles", false);

    public final NumberSetting cps = new NumberSetting("CPS", 10, 0, 20, 1);
    public final ModeSetting attackEvent = new ModeSetting("AttackEvent", "Legit", "Legit", "PreUpdate");

    public final BooleanSetting mouseSensitivity = new BooleanSetting("MouseSensitivity", false);
    public final BooleanSetting a3 = new BooleanSetting("A3Fix", false).visibility(mouseSensitivity::getValue);

    private final RotationProcessor.SensitivityPatch sensitivityPatch = new RotationProcessor.SensitivityPatch(false);

    private EntityLivingBase target;
    private Vec3 targetClosestVector;

    public KillAuraHack() {
        this.extensions.add(new AutoClickerExtension(this::canAttack, this::getCps, this::attack, () ->
                switch (attackEvent.getValue()) {
                    case "Legit" -> AutoClickerExtension.ClickEvent.LEGIT;
                    case "PreUpdate" -> AutoClickerExtension.ClickEvent.PRE_UPDATE;
                    default -> throw new UnsupportedOperationException("Mode value unaccounted for");
                }
        ));
    }

    @EventHandler
    public final void onRotation(PlayerRotationEvent playerRotationEvent) {
        final Vec3 look = mc.thePlayer.getPositionEyes(1F);

        if (targetReloadEvent.is("PreRotation"))
            reloadTarget(look);

        if (target != null) {
            final float[] rotation = MovementUtil.Math.getAngle(targetClosestVector);

            playerRotationEvent.setRotationYaw(rotation[0]);
            playerRotationEvent.setRotationPitch(rotation[1]);

            if (this.mouseSensitivity.getValue()) {
                sensitivityPatch.a3 = this.a3.getValue();
                playerRotationEvent.modifiers.add(sensitivityPatch);
            }
        }
    }

    @EventHandler
    public final void onAttack(PreAttackEvent preAttackEvent) {
        if (targetReloadEvent.is("PreAttack"))
            reloadTarget(mc.thePlayer.getPositionEyes(1F));
    }

    @EventHandler
    public final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
        if (playerUpdateEvent.type != PlayerType.SERVER || playerUpdateEvent.stage != Stage.PRE)
            return;

        if (targetReloadEvent.is("PreUpdate"))
            reloadTarget(mc.thePlayer.getPositionEyes(1F));
    }

    public void reloadTarget(Vec3 look) {
        if (target != null && !switchTargets.getValue()) {
            final Vec3 closest = BBMathUtil.getClosestPoint(look, target.getEntityBoundingBox());

            if (mc.theWorld.getLoadedEntityList().contains(target) && look.distanceTo(closest) <= targetRange.getValue()) {
                targetClosestVector = closest;
                return;
            } else {
                target = null;
                targetClosestVector = null;
            }
        }

        final List<Pair<EntityLivingBase, Vec3>> possible = new LinkedList<>();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (!(entity instanceof EntityLivingBase castEntity))
                continue;

            if (entity == mc.thePlayer)
                continue;

            if (entity.isInvisible() && !invisibles.getValue())
                continue;

            typeCheck: {
                if (entity instanceof EntityPlayer && players.getValue())
                    break typeCheck;

                if (entity instanceof EntityMob && monsters.getValue())
                    break typeCheck;

                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && passives.getValue())
                    break typeCheck;

                continue;
            }

            final Vec3 bestVector = BBMathUtil.getClosestPoint(look, entity.getEntityBoundingBox());

            if (look.distanceTo(bestVector) > targetRange.getValue())
                continue;

            if (entity instanceof EntityPlayer entityPlayer) {
                final CombatProcessor.ValidEntityEvent validEntityEvent = new CombatProcessor.ValidEntityEvent(entityPlayer);
                Argon.getInstance().eventBus.post(validEntityEvent);

                if (!validEntityEvent.valid)
                    continue;
            }

            possible.add(new ImmutablePair<>(castEntity, bestVector));
        }

        possible.sort(Comparator.comparingDouble(pair -> pair.getLeft().getHealth()));

        if (!possible.isEmpty()) {
            final Pair<EntityLivingBase, Vec3> first = possible.getFirst();

            target = first.getLeft();
            targetClosestVector = first.getRight();
        } else {
            target = null;
            targetClosestVector = null;
        }
    }

    public boolean canAttack() {
        return target != null;
    }

    public float getCps() {
        return cps.getValue();
    }

    public boolean attack() {
        mc.clickMouse();
        return true;
    }

}
