package net.jezevcik.argon.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeybindingAccessor {

    @Accessor("boundKey")
    InputUtil.Key getBoundKey();

    @Accessor("pressed")
    boolean isPressedA();

    @Accessor
    void setTimesPressed(int timesPressed);

    @Accessor
    int getTimesPressed();

}
