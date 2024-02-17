package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.PlaySound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public class HopperEntityMixin {

    @Inject(method = "getContainerAt(Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/Container;",
            at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void getContainerAt(Level $$0, double $$1, double $$2, double $$3, CallbackInfoReturnable<Container> cir, Container container) {
        if (container == null) {
            List<Entity> $$9 = $$0.getEntities((Entity)null, new AABB($$1 - 0.5, $$2 - 0.5, $$3 - 0.5, $$1 + 0.5, $$2 + 0.5, $$3 + 0.5), in -> in instanceof BackpackEntity);
            if (!$$9.isEmpty()) {
                BackpackEntity backpackEntity = (BackpackEntity) $$9.get($$0.random.nextInt($$9.size()));
                cir.setReturnValue(backpackEntity.backpackInventory);
            }
        }
    }

    @Shadow
    private static boolean isEmptyContainer(Container $$0, Direction $$1) {
        return false;
    }

    @Inject(method = "suckInItems", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/core/Direction;DOWN:Lnet/minecraft/core/Direction;"))
    private static void tryTakeFromBackpack(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> cir, Container container) {
        if (container instanceof BackpackInventory backpackInventory) {
            Direction direction = Direction.DOWN;
            boolean r = !isEmptyContainer(container, direction) && backpackInventory.hopperTakeOne(hopper);
            if (r)
                PlaySound.TAKE.at(backpackInventory.getOwner(), backpackInventory.getLocalData().kind(), 0.8f);
            cir.setReturnValue(r);
        }
    }
    @Inject(method = "ejectItems", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;getContainerSize()I"))
    private static void tryEjectToBackpack(Level $$0, BlockPos $$1, BlockState $$2, Container hopper, CallbackInfoReturnable<Boolean> cir, Container container, Direction $$5, int $$6) {
        if (container instanceof BackpackInventory backpackInventory) {
            boolean r = backpackInventory.hopperInsertOne(hopper);
            cir.setReturnValue(r);
        }
    }
    @Inject(method = "getSlots", cancellable = true, at = @At("HEAD"))
    private static void addBackpackSlotsCheck(Container container, Direction $$1, CallbackInfoReturnable<IntStream> cir) {
        if (container instanceof BackpackInventory backpackInventory && backpackInventory.isEmpty()) {
            cir.setReturnValue(IntStream.of(0));
        }
    }
}
