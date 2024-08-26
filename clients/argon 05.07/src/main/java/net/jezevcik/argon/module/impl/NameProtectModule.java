package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.TextSetting;
import net.jezevcik.argon.event.impl.RenderTextEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Language;

import java.awt.*;

public class NameProtectModule extends Module {

    public final TextSetting playerUserName = new TextSetting("PlayerUsername", "Argon User", this.config);

    public final BooleanSetting hideEveryone = new BooleanSetting("HideEveryone", true, this.config);

    public final TextSetting otherUserName = new TextSetting("OtherUsername", "Protected User", this.config)
            .visibility(SupplierFactory.setting(hideEveryone, true, true));

    public NameProtectModule() {
        super(ModuleParams.builder()
                .name("NameProtect")
                .category(ModuleCategory.RENDER)
                .build());
    }

    @EventHandler
    public final void onText(RenderTextEvent renderTextEvent) {
        if (!Minecraft.inGame())
            return;

        if (renderTextEvent.text instanceof String string) {
            renderTextEvent.text = replace(string);
        } else if (renderTextEvent.text instanceof OrderedText orderedText) {
            final MutableText text = Text.literal("");

            orderedText.accept(new CharacterVisitor() {
                @Override
                public boolean accept(int index, Style style, int codePoint) {
                    text.append(((MutableText) Text.of(String.valueOf((char) codePoint))).setStyle(style));
                    return true;
                }
            });


            renderTextEvent.text = Text.literal(replace(text.getString())).asOrderedText();
        }
    }

    private String replace(String s) {
        s = s.replaceAll(client.player.getName().getLiteralString(), playerUserName.getValue());

        if (hideEveryone.getValue()) {
            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof PlayerEntity) {
                    s = s.replaceAll(entity.getName().getLiteralString(), otherUserName.getValue());
                }
            }
        }

        return s;
    }

}
