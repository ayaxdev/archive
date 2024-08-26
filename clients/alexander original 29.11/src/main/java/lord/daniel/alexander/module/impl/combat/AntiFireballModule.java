package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.*;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.math.time.TimeHelper;
import lord.daniel.alexander.util.rotation.RotationUtil;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "AntiFireball", categories = {EnumModuleType.COMBAT, EnumModuleType.GHOST})
public class AntiFireballModule extends AbstractModule {

    private final NumberValue<Float> radius = new NumberValue<>("Radius", this, 5f, 0f, 6f);
    private final MultiSelectValue clickEvent = new MultiSelectValue("ClickEvent", this, new String[]{"OnClicking", "OnTick"}, new String[]{"OnClicking", "OnTick", "OnPreUpdate", "OnPostUpdate", "OnPreMotion", "OnPostMotion"});
    private final BooleanValue rotate = new BooleanValue("Rotate", this, true);
    private final BooleanValue rayTrace = new BooleanValue("RayTrace", this, true);
    private final BooleanValue stopMove = new BooleanValue("StopMovement", this, true);

    private TimeHelper timeHelper = new TimeHelper();
    private boolean attacking = false;
    private EntityFireball entity;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(radius.getValueAsString());
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        switch (updateMotionEvent.getStage()) {
            case PRE -> {
                if (clickEvent.is("OnPreUpdate"))
                    onClick();
            }
            case MID -> {
                if (clickEvent.is("OnPostUpdate") || clickEvent.is("OnPreMotion"))
                    onClick();
            }
            case POST -> {
                if (clickEvent.is("OnPostMotion"))
                    onClick();
            }
        }
    };

    @EventLink
    public final Listener<OnTickEvent> onTickEventListener = event -> {
        if (clickEvent.is("OnTick"))
            onClick();
    };

    @EventLink
    public final Listener<ClickingEvent> clickingEventListener = e -> {
        if(clickEvent.is("OnClicking"))
            onClick();
    };

    @EventLink
    public final Listener<RotationEvent> rotationEventListener = rotationEvent -> {
        if(entity != null && rotate.getValue()) {
            float[] rotations = RotationUtil.getRotation(new Vec3(entity.posX, entity.posY, entity.posZ));
            rotations = RotationUtil.applyMouseFix(rotations[0], rotations[1]);

            rotationEvent.setYaw(rotations[0]);
            rotationEvent.setPitch(rotations[1]);
        }
    };

    @EventLink
    public final Listener<MoveFlyingEvent> moveFlyingEventListener = moveFlyingEvent -> {
        if(attacking && stopMove.getValue()) {
            attacking = false;
            moveFlyingEvent.setForward(0);
            moveFlyingEvent.setStrafe(0);
            moveFlyingEvent.setFriction(0);
        }
    };

    public void onClick() {
        for (Object entityObj : mc.theWorld.loadedEntityList) {
            if (entityObj instanceof EntityFireball) {
                this.entity = (EntityFireball) entityObj;
                if (mc.thePlayer.getDistanceToEntity(entity) < radius.getValue() && timeHelper.hasReached(300) && (!rayTrace.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit.getEntityId() == entity.getEntityId()))) {
                    attacking = true;

                    mc.thePlayer.swingItem();

                    mc.playerController.attackEntity(mc.thePlayer, entity);

                    timeHelper.reset();
                    break;
                }
            }
        }
        this.entity = null;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
