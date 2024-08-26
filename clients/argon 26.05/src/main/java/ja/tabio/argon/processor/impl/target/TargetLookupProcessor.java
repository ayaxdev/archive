package ja.tabio.argon.processor.impl.target;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.Event;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.PlayerTickEvent;
import ja.tabio.argon.event.impl.TickEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.RegisterProcessor;
import ja.tabio.argon.utils.math.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@RegisterProcessor
public class TargetLookupProcessor extends Processor {

    private final List<StoredEntity> storedEntities = new ArrayList<>();

    public TargetLookupProcessor() {
        super("TargetLookup");
    }

    @EventHandler
    public final void onTick(TickEvent tickEvent) {
        if (!Minecraft.inGame()) {
            storedEntities.clear();
        }
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent tickEvent) {
        if (tickEvent.type != PlayerType.LOCAL || tickEvent.stage != Stage.PRE)
            return;

        assert mc.player != null;
        assert mc.world != null;

        final List<StoredEntity> currentStoredEntities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity) || entity == mc.player)
                continue;

            final TargetCheckEvent targetCheckEvent = new TargetCheckEvent(entity);
            Argon.getInstance().eventBus.post(targetCheckEvent);

            if (targetCheckEvent.cancelled)
                continue;

            final Vec3d closest = MathUtils.AABB.getBestAimPoint(livingEntity.getBoundingBox());
            final double distance = mc.player.getEyePos().distanceTo(closest);

            final EntityType entityType = switch (entity) {
                case PlayerEntity ignored -> EntityType.PLAYER;
                case HostileEntity ignored -> EntityType.MONSTER;
                case PassiveEntity ignored -> EntityType.PASSIVE;
                default -> EntityType.OTHER;
            };

            currentStoredEntities.add(new StoredEntity(livingEntity, distance, closest, entityType));
        }

        this.storedEntities.clear();
        this.storedEntities.addAll(currentStoredEntities);
    }

    public List<StoredEntity> getInReach(double reach) {
        return this.storedEntities.stream().filter(storedEntity -> storedEntity.inReach(reach)).toList();
    }

    public record StoredEntity(LivingEntity entity, double distance, Vec3d closest, EntityType entityType) {

        public boolean inReach(double reach) {
            return distance <= reach;
        }

    }

    public static class TargetCheckEvent extends Event {
        public final Entity entity;

        public TargetCheckEvent(Entity entity) {
            this.entity = entity;
        }
    }

    public enum EntityType {
        PLAYER,
        PASSIVE,
        MONSTER,
        OTHER;
    }

}
