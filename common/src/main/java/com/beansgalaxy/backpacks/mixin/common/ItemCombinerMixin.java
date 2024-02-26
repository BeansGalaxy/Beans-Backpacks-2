package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.items.EnderBackpack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMixin extends AbstractContainerMenu {

      protected ItemCombinerMixin(@Nullable MenuType<?> $$0, int $$1) {
            super($$0, $$1);
      }

      @Shadow public abstract int getResultSlot();

      @Inject(method = "quickMoveStack", at = @At(value = "HEAD"))
      private void updateEnderDataOnQuickMove(Player player, int slot, CallbackInfoReturnable<ItemStack> cir) {
            int resultSlot = this.getResultSlot();
            ItemStack stack;
            if (slot == resultSlot && (stack = this.slots.get(resultSlot).getItem()).getItem() instanceof EnderBackpack enderBackpack) {
                  enderBackpack.onCraftedBy(stack, player.level(), player);
            }
      }
}
