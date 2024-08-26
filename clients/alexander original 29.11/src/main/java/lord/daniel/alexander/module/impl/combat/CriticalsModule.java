package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.AttackEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "Criticals", enumModuleType = EnumModuleType.COMBAT)
public class CriticalsModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "VanillaPacket", new String[]{"VanillaPacket", "PacketJump", "NCPLatest", "Visual"});
    private final NumberValue<Float> diff = new NumberValue<>("PacketDifference", this, 0.05f, 0.001f, 1f, 3).addVisibleCondition(() -> mode.is("PacketJump"));

    private int attacked = 0;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(mode.getValue());
    };

    @EventLink
    public final Listener<AttackEvent> attackEventListener = attackEvent -> {
        if(!mc.thePlayer.onGround)
            return;

        attacked++;

        switch (mode.getValue()) {
            case "Visual" -> {
                mc.thePlayer.onCriticalHit(attackEvent.getAttacking());
            }
            case "VanillaPacket" -> {
                sendOffsetPositionUnlogged(0, 0.0626, 0, false);
                sendOffsetPositionUnlogged(0, 17.64e-8, 0, false);
            }
            case "PacketJump" -> {
                boolean finishedJump = false;
                for (double yOffset = 0.01; yOffset > 0; yOffset += (finishedJump ? -diff.getValue() : diff.getValue())) {
                    if (yOffset >= 0.42f) {
                        finishedJump = true;
                        yOffset = 0.42f;
                    }
                    sendPositionUnlogged(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ, false);
                }
                sendPositionUnlogged(mc.thePlayer.posX, mc.thePlayer.posY + 0.001, mc.thePlayer.posZ, false);
            }
            case "NCPLatest" -> {
                if(attacked % 5f == 0) {
                    sendOffsetPositionUnlogged(0, 0.00001058293536, 0, false);
                    sendOffsetPositionUnlogged(0, 0.00000916580235, 0, false);
                    sendOffsetPositionUnlogged(0, 0.00000010371854, 0, false);
                }
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
