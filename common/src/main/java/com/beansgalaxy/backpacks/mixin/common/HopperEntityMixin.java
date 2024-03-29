package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
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
    private static void getContainerAt(Level level, double x, double y, double z, CallbackInfoReturnable<Container> cir, Container container) {
        if (container == null) {
            List<Entity> $$9 = level.getEntities((Entity)null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), in -> in instanceof EntityAbstract);
            if (!$$9.isEmpty()) {
                EntityAbstract entityAbstract = (EntityAbstract) $$9.get(level.random.nextInt($$9.size()));
                cir.setReturnValue(entityAbstract.getInventory());
            }
        }
    }

    @Inject(method = "getSlots", cancellable = true, at = @At("HEAD"))
    private static void addBackpackSlotsCheck(Container container, Direction $$1, CallbackInfoReturnable<IntStream> cir) {
        if (container instanceof BackpackInventory backpackInventory) {
            int containerSize = backpackInventory.getContainerSize();
            if (backpackInventory.spaceLeft() < 1) {
                containerSize -= 2;
            }
            IntStream range = IntStream.range(0, containerSize + 1);
            cir.setReturnValue(range);

        }
    }

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void cancelCauldronPotPickup(Container $$0, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        CompoundTag backSlot = itemEntity.getItem().getTagElement("back_slot");
        if (backSlot != null)
            cir.setReturnValue(false);
    }
}
