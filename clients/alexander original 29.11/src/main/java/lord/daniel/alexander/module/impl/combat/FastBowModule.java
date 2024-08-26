package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.LivingUpdateEvent;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "FastBow", enumModuleType = EnumModuleType.COMBAT)
public class FastBowModule extends AbstractModule {

    private final NumberValue<Integer> packetsValue = new NumberValue<>("Packets", this, 20, 3, 20);

    @EventLink
    public final Listener<LivingUpdateEvent> livingUpdateEventListener = livingUpdateEvent -> {
        if (!mc.thePlayer.isUsingItem()) {
            return;
        }

        ItemStack currentItem = mc.thePlayer.getHeldItem();

        if (currentItem != null && currentItem.getItem() instanceof ItemBow) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, currentItem, 0.0F, 0.0F, 0.0F));

            float yaw = PlayerHandler.yaw;
            float pitch = PlayerHandler.pitch;

            for (int i = 0; i < packetsValue.getValue(); i++) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, true));
            }

            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.thePlayer.itemInUseCount = currentItem.getMaxItemUseDuration() - 1;
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
