package com.atani.nextgen.module.impl.combat;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.component.AutoClickerComponent;
import com.atani.nextgen.event.impl.PlayerPacketsEvent;
import com.atani.nextgen.event.impl.RotationEvent;
import com.atani.nextgen.event.impl.SetTargetEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.impl.CheckBuilder;
import com.atani.nextgen.setting.builder.impl.SliderBuilder;
import com.atani.nextgen.util.player.PlayerUtil;
import com.atani.nextgen.util.player.RotationUtil;
import io.github.racoondog.norbit.EventHandler;
import io.github.racoondog.norbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public final class KillAuraModule extends ModuleFeature {

    public final SettingFeature<Float> cps = new SliderBuilder("CPS", 10, 0, 20, 1).build();

    public final SettingFeature<Float> range = new SliderBuilder("Range", 3, 0, 6, 1).build();

    public final SettingFeature<Boolean> players = new CheckBuilder("Players", true).build();
    public final SettingFeature<Boolean> monsters = new CheckBuilder("Monsters", true).build();
    public final SettingFeature<Boolean> animals = new CheckBuilder("Animals", false).build();
    public final SettingFeature<Boolean> switchTargets = new CheckBuilder("SwitchTargets", false).build();

    private EntityLivingBase target;

    private final Runnable checker = () -> {
        if(target != null && !validEntity(target))
            target = null;
    };

    public KillAuraModule() {
        super(new ModuleBuilder("KillAura", "Attacks entities", ModuleCategory.COMBAT));

        this.components.add(new AutoClickerComponent(() -> target != null, this::getCps, this::onClick));
    }
    

    @EventHandler
    public void onUpdate(PlayerPacketsEvent playerPacketsEvent) {
        AtaniClient.getInstance().threadpool.submit(checker);
        
        final ArrayList<Entity> possibleTargets = new ArrayList<>();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if(validEntity(entity))
                possibleTargets.add(entity);
        }

        possibleTargets.sort((o1, o2) -> (int) (mc.thePlayer.getDistanceToEntity(o1) - mc.thePlayer.getDistanceToEntity(o2)));

        if (!possibleTargets.isEmpty()) {
            final EntityLivingBase nextTarget = (EntityLivingBase) possibleTargets.get(0);
            boolean setTarget = target == null || switchTargets.getValue();

            if(setTarget) {
                target = nextTarget;
            }
        };
    }

    @EventHandler
    public void onRotation(RotationEvent rotationEvent) {
        if(target != null) {
            final Vec3 aimVector = RotationUtil.getBestLookVector(mc.thePlayer.getPositionEyes(1F), target.getEntityBoundingBox());
            float[] rotations = RotationUtil.getRotation(aimVector);

            rotationEvent.rotationYaw = rotations[0];
            rotationEvent.rotationPitch = rotations[1];
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSetTarget(SetTargetEvent setTargetEvent) {
        setTargetEvent.setNextTarget(target);
    }

    // TODO: does nothing rn, will be useful later
    private float getCps() {
        return cps.getValue();
    }

    private void onClick() {
        mc.clickMouse();
    }
    
    private boolean validEntityType(Entity e) {
        if(e == null || mc.theWorld == null || mc.thePlayer == null)
            return false;

        if(!(e instanceof EntityLivingBase))
            return false;

        if (((EntityLivingBase) e).getHealth() == 0)
            return false;

        if(e instanceof EntityPlayer && e != mc.thePlayer && players.getValue())
            return true;

        if(e instanceof EntityMob && monsters.getValue())
            return true;

        return e instanceof EntityAnimal && animals.getValue();
    }

    private boolean validEntity(Entity entity) {
        if (!validEntityType(entity))
            return false;

        // Do faster, Euclidean distance calculation between positions to eliminate 99% of non-targetable entities
        if (mc.thePlayer.getDistanceToEntity(entity) > (range.getValue() + 2.0F))
            return false;

        return !(PlayerUtil.getLookRangeToEntity(entity) > range.getValue());
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {

    }

}
