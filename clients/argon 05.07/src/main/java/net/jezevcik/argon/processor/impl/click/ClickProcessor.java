package net.jezevcik.argon.processor.impl.click;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.ClickProcessingEvent;
import net.jezevcik.argon.event.impl.ClickReprocessingEvent;
import net.jezevcik.argon.event.impl.KeyPressedStateEvent;
import net.jezevcik.argon.mixin.KeybindingAccessor;
import net.jezevcik.argon.processor.Processor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;

public class ClickProcessor extends Processor implements ClickCallback {

    private boolean enableLeft;
    private boolean enableRight;

    public ClickProcessor() {
        super("Clicks");
    };

    @EventHandler
    public final void onClickProcessing(ClickProcessingEvent clickProcessingEvent) {
        enableLeft = false;
        enableRight = false;

        ParekClient.getInstance().eventBus.post(new ClickReprocessingEvent(this));
    }

    @EventHandler
    public void onKeyPressedState(KeyPressedStateEvent keyPressedStateEvent) {
        if (keyPressedStateEvent.keyBinding == client.options.attackKey && enableLeft || keyPressedStateEvent.keyBinding == client.options.useKey && enableRight)
            keyPressedStateEvent.pressed = true;
    }

    @Override
    public void left() {
        if (client.attackCooldown > 0) return;

        final KeybindingAccessor attackKey = (KeybindingAccessor) client.options.attackKey;
        attackKey.setTimesPressed(attackKey.getTimesPressed() + 1);
        enableLeft = true;
    }

    @Override
    public void right() {
        final KeybindingAccessor useKey = (KeybindingAccessor) client.options.useKey;
        useKey.setTimesPressed(useKey.getTimesPressed() + 1);
        enableRight = true;
    }

    @Override
    public void attackBlock(BlockPos pos, Direction side) {
        client.interactionManager.attackBlock(pos, side);
    }

    @Override
    public void useBlock(BlockPos pos, Hand hand, BlockHitResult hitResult) {
        ItemStack itemStack = client.player.getStackInHand(hand);

        int count = itemStack.getCount();
        ActionResult interactBlock = client.interactionManager.interactBlock(client.player, hand, hitResult);
        if (interactBlock.isAccepted()) {
            if (interactBlock.shouldSwingHand()) {
                client.player.swingHand(hand);
                if (!itemStack.isEmpty() && (itemStack.getCount() != count || client.interactionManager.hasCreativeInventory())) {
                    client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                }
            }
            return;
        }

        if (interactBlock == ActionResult.FAIL) {
            return;
        }

        if (!itemStack.isEmpty()) {
            ActionResult interactItem = client.interactionManager.interactItem(client.player, hand);
            if (interactItem.isAccepted()) {
                if (interactItem.shouldSwingHand()) {
                    client.player.swingHand(hand);
                }
                client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            }
        }
    }

    @Override
    public void attackEntity(Entity entity) {
        client.interactionManager.attackEntity(client.player, entity);
    }

}
