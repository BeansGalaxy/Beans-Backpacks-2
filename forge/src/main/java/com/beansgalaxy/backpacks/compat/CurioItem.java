package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.core.BackData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
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
            return ICurioItem.super.canEquip(slotContext, stack) && slotContext.index() == 0;
      }

      @Override
      public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
            if (slotContext.entity() instanceof Player player)
                  return BackData.get(player).backpackInventory.isEmpty();
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
            return slotContext.entity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ?
                        ICurio.DropRule.ALWAYS_KEEP :
                        ICurio.DropRule.DESTROY;
      }
}