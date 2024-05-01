package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
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
      public boolean canEquip(SlotContext slotContext, ItemStack stack) {
            boolean defaults = ICurioItem.super.canEquip(slotContext, stack) && slotContext.index() == 0;
            boolean backSlotDisabled = slotContext.entity() instanceof Player player && BackData.get(player).mayEquip(stack, true);
            return defaults && !backSlotDisabled && stack.getCount() > 1;
      }

      @Override
      public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
            if (slotContext.entity() instanceof Player player)
                  return BackData.get(player).getBackpackInventory().isEmpty();
            return true;
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

      @NotNull
      @Override
      public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
            return ICurio.DropRule.ALWAYS_KEEP;
      }
}