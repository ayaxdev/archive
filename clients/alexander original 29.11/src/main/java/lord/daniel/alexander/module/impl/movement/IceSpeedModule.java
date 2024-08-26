package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.world.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.init.Blocks;

@ModuleData(name = "IceSpeed", enumModuleType = EnumModuleType.MOVEMENT)
public class IceSpeedModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Custom", new String[]{"Custom", "Verus", "Test"});
    private final NumberValue<Float> slipperiness = new NumberValue<>("Slipperiness", this, 0.1f, 0.1f, 1f).addVisibleCondition(() -> mode.is("Custom"));

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(String.valueOf(slipperiness.getValue()));
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        float slipperiness = switch (mode.getValue()) {
            case "Verus" -> mc.thePlayer.ticksExisted % 10 == 0 ? 0.29f : 0.5f;
            default -> this.slipperiness.getValue();
        };

        switch (mode.getValue()) {
            case "Verus" -> {
                Block blockUnder = WorldUtil.getBlockUnderPlayer();

                if(blockUnder instanceof BlockIce || blockUnder instanceof BlockPackedIce) {
                    if(mc.thePlayer.ticksExisted % 12 == 0) {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    }
                }
            }
        }

        Blocks.ice.slipperiness = (float) slipperiness;
        Blocks.packed_ice.slipperiness = (float) slipperiness;
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
    }
}
