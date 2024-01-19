package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.core.BackData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioItem implements ICurioItem {

      @Override
      public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
            if (slotContext.entity() instanceof Player player)
                  BackData.get(player).update(stack);
      }

      @Override
      public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack oldStack) {
            if (slotContext.entity() instanceof Player player)
                  BackData.get(player).update(newStack);
      }

      @Override
      public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
            boolean b = ICurioItem.super.canUnequip(slotContext, stack);
            if (slotContext.entity() instanceof Player player)
                  return BackData.get(player).backpackInventory.isEmpty() && b;
            return b;
      }

      @Override
      public void curioTick(SlotContext slotContext, ItemStack stack) {
            if (slotContext.entity() instanceof Player player) {
                  BackData backSlot = BackData.get(player);
                  if (backSlot.getStack() != stack) {
                        backSlot.update(stack);
                  }
            }
      }
}