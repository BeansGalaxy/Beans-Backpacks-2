package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

      @Shadow @Final @Deprecated @Nullable private Item item;

      @Shadow public abstract Item getItem();

      @Unique
      private final ItemStack instance = ((ItemStack) (Object) this);

      @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(ItemStack stack, Slot slot, ClickAction clickAction, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
            if (BackpackItem.interact(instance, clickAction, player, access, false))
                  cir.setReturnValue(true);
      }

      @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
            Optional<TooltipComponent> tooltip = Tooltip.get(instance);
            if (!tooltip.equals(Optional.empty()))
                  cir.setReturnValue(tooltip);
      }

      @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                  target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
      private void redirectBackSlotTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> components, MutableComponent name) {
            if (player == null)
                  return;

            BackData backData = BackData.get(player);
            if (instance == backData.getStack() && backData.backpackInventory.isEmpty())
            {
                  boolean isBackpack = getItem() instanceof BackpackItem;
                  boolean isPot = instance.is(Items.DECORATED_POT);
                  if (backData.actionKeyPressed) {
                        if (isBackpack)
                              cir.setReturnValue(Tooltip.addLore(components, "backpack", 5));
                        else if (isPot)
                              cir.setReturnValue(Tooltip.addLore(components, "pot", 7));
                  }
                  else if (isBackpack || isPot)
                        Tooltip.loreTitle(components);
            }
      }
}
