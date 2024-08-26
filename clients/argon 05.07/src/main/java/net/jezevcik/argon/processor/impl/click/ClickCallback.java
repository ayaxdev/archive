package net.jezevcik.argon.processor.impl.click;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface ClickCallback {

    void left();

    void right();

    void attackBlock(BlockPos pos, Direction side);

    void useBlock(BlockPos pos, Hand hand, BlockHitResult hitResult);

    void attackEntity(Entity entity);

}