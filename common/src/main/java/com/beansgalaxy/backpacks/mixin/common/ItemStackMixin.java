package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {

      @Unique
      private final ItemStack instance = ((ItemStack) (Object) this);

      @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(ItemStack stack, Slot slot, ClickAction clickAction, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
            if (BackpackItem.stackedOnMe(instance, stack, clickAction, player, access))
                  cir.setReturnValue(true);
      }

      @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
            Optional<TooltipComponent> tooltip = Tooltip.get(instance);
            if (!tooltip.equals(Optional.empty()))
                  cir.setReturnValue(tooltip);
      }

}
