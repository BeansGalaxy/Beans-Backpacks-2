package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.events.PlaySound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HopperBlockEntity.class)
public class HopperEntityMixin {
    @Inject(method = "ejectItems", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerSize()I"))
    private static void tryEjectToBackpack(Level p_155563_, BlockPos p_155564_, BlockState p_155565_, HopperBlockEntity hopper, CallbackInfoReturnable<Boolean> cir, Container container, Direction direction, int i) {
        if (container instanceof BackpackInventory backpackInventory) {
            boolean r = backpackInventory.hopperInsertOne(hopper);
            cir.setReturnValue(r);
        }
    }

    @Shadow
    private static boolean isEmptyContainer(Container $$0, Direction $$1) {
        return false;
    }

    @Inject(method = "suckInItems", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/core/Direction;DOWN:Lnet/minecraft/core/Direction;"))
    private static void tryTakeFromBackpack(Level p_155553_, Hopper hopper, CallbackInfoReturnable<Boolean> cir, Boolean ret, Container container) {
        if (container instanceof BackpackInventory backpackInventory) {
            Direction direction = Direction.DOWN;
            boolean r = !isEmptyContainer(container, direction) && backpackInventory.hopperTakeOne(hopper);
            if (r) backpackInventory.playSound(PlaySound.TAKE);
            cir.setReturnValue(r);
        }
    }
}
