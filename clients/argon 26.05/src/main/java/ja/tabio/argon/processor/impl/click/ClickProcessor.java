package ja.tabio.argon.processor.impl.click;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.HandleInputEventsEvent;
import ja.tabio.argon.event.impl.KeyPressedStateEvent;
import ja.tabio.argon.mixin.KeybindingAccessor;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.RegisterProcessor;
import ja.tabio.argon.processor.impl.click.impl.Clicker;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@RegisterProcessor
public class ClickProcessor extends Processor {

    private final List<Clicker> clickers = new ArrayList<>();

    private boolean enableLeft;
    private boolean enableRight;

    public ClickProcessor() {
        super("Click");
    }

    public void add(Clicker clicker) {
        this.clickers.add(clicker);
    }

    @EventHandler
    public final void onHandleInputEvents(HandleInputEventsEvent handleInputEventsEvent) {
        enableLeft = false;
        enableRight = false;

        for (Clicker clicker : clickers)
            clicker.runClick(this);
    }

    @EventHandler
    public final void onKeyPressedState(KeyPressedStateEvent keyPressedStateEvent) {
        if (keyPressedStateEvent.keyBinding == mc.options.attackKey && enableLeft || keyPressedStateEvent.keyBinding == mc.options.useKey && enableRight)
            keyPressedStateEvent.pressed = true;
    }

    public void left() {
        if (mc.attackCooldown > 0) return;
        ((KeybindingAccessor) mc.options.attackKey).setTimesPressed(((KeybindingAccessor) mc.options.attackKey).getTimesPressed() + 1);
        enableLeft = true;
    }

    public void right() {
        ((KeybindingAccessor) mc.options.useKey).setTimesPressed(((KeybindingAccessor) mc.options.useKey).getTimesPressed() + 1);
        enableRight = true;
    }

    public void attackBlock(BlockPos pos, Direction side) {
        if(mc.interactionManager == null || mc.player == null)
            return;

        mc.interactionManager.attackBlock(pos, side);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public void useBlock(BlockPos pos, Hand hand, BlockHitResult hitResult) {
        if(mc.interactionManager == null || mc.player == null)
            return;

        ItemStack itemStack = mc.player.getStackInHand(hand);

        int count = itemStack.getCount();
        ActionResult interactBlock = mc.interactionManager.interactBlock(mc.player, hand, hitResult);
        if (interactBlock.isAccepted()) {
            if (interactBlock.shouldSwingHand()) {
                mc.player.swingHand(hand);
                if (!itemStack.isEmpty() && (itemStack.getCount() != count || mc.interactionManager.hasCreativeInventory())) {
                    mc.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                }
            }
            return;
        }

        if (interactBlock == ActionResult.FAIL) {
            return;
        }

        if (!itemStack.isEmpty()) {
            ActionResult interactItem = mc.interactionManager.interactItem(mc.player, hand);
            if (interactItem.isAccepted()) {
                if (interactItem.shouldSwingHand()) {
                    mc.player.swingHand(hand);
                }
                mc.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            }
        }
    }

    public void attackEntity(Entity entity) {
        if(mc.interactionManager == null || mc.player == null)
            return;

        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

}
