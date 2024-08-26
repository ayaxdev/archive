package ja.tabio.argon.module.impl.combat;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.AttackEvent;
import ja.tabio.argon.event.impl.ReceivePacketEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.processor.impl.target.TargetLookupProcessor;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@RegisterModule
public class AntiBot extends Module {

    public final BooleanSetting manualHit = new BooleanSetting("ManualHit", false);

    public final BooleanSetting ticksExisted = new BooleanSetting("TicksExisted", false);
    public final NumberSetting ticks = new NumberSetting("Ticks", 20, 0, 100, 0)
            .visibility(ticksExisted);

    public final BooleanSetting sound = new BooleanSetting("Sound", false);
    public final NumberSetting soundDistance = new NumberSetting("SoundDistance", 1, 0, 1, 1)
            .visibility(sound);
    public final NumberSetting minimumVolume = new NumberSetting("MinimumVolume", 0, 0, 1, 1)
            .visibility(sound);

    public final List<Entity> manualHitEntities = new ArrayList<>(),
                    passedSoundEntities = new ArrayList<>();

    public AntiBot() {
        super(ModuleParams.builder()
                .name("AntiBot")
                .category(ModuleCategory.COMBAT)
                .build());
    }

    @EventHandler
    public final void onTargetCheck(TargetLookupProcessor.TargetCheckEvent targetCheckEvent) {
        final Entity entity = targetCheckEvent.entity;

        assert entity != null;

        if (targetCheckEvent.entity instanceof PlayerEntity player) {
            if (sound.getValue() && !passedSoundEntities.contains(player)) {
                targetCheckEvent.cancelled = true;
                return;
            }
        }

        if (manualHit.getValue() && !manualHitEntities.contains(targetCheckEvent.entity)) {
            targetCheckEvent.cancelled = true;
            return;
        }

        if (ticksExisted.getValue() && entity.age < ticks.getValue()) {
            targetCheckEvent.cancelled = true;
            return;
        }
    }

    @EventHandler
    public final void onAttack(AttackEvent attackEvent) {
        if (!manualHitEntities.contains(attackEvent.entity))
            manualHitEntities.add(attackEvent.entity);
    }

    @EventHandler
    public final void onReceive(ReceivePacketEvent receivePacketEvent) {
        if (!Minecraft.inGame())
            return;

        assert mc.world != null;

        if (receivePacketEvent.packet instanceof PlaySoundS2CPacket packet) {
            if (!sound.getValue())
                return;

            if (packet.getVolume() > minimumVolume.getValue()) {
                for (Entity entity : mc.world.getEntities()) {
                    if (entity instanceof PlayerEntity && !passedSoundEntities.contains(entity) && entity.getPos().squaredDistanceTo(new Vec3d(packet.getX(), packet.getY(), packet.getZ())) <= soundDistance.getValue() * soundDistance.getValue())
                        passedSoundEntities.add(entity);
                }
            }
        }
    }

}
