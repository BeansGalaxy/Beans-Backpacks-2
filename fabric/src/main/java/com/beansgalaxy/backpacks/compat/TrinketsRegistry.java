package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import dev.emi.trinkets.api.*;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

import java.util.Map;
import java.util.Optional;

public class TrinketsRegistry {
      public static void register() {
            Trinket trinket = new Trinket() {
                  @Override
                  public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        return TrinketEnums.DropRule.KEEP;
                  }

                  @Override
                  public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        boolean b = Trinket.super.canEquip(stack, slot, entity) && slot.index() == 0;
                        boolean b1 = !Services.COMPAT.backSlotDisabled(entity);
                        return b && b1;

                  }

                  @Override
                  public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player)
                              return BackData.get(player).backSlot.mayPickup(player);
                        return Trinket.super.canUnequip(stack, slot, entity);
                  }
            };

            for (Kind kind: Kind.values())
                  TrinketsApi.registerTrinket(kind.getItem(), trinket);

            TrinketsApi.registerTrinketPredicate(
                        new ResourceLocation("trinkets", "all"), (stack, ref, entity) -> {
                              if (entity instanceof Player player && !Kind.isBackpack(stack)) {
                                    if (Kind.isWearable(BackData.get(player).getStack())
                                                && Constants.DISABLES_BACK_SLOT.contains(stack.getItem())) {
                                          return TriState.FALSE;
                                    }
                              }
                              return TriState.TRUE;
                        });

            //TrinketsApi.registerTrinket(Items.ELYTRA.asItem(), trinket);
      }

      public static void setBackStack(ItemStack stack, BackData backData) {
            Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(backData.owner);
            if (trinketComponent.isEmpty())
                  return;

            Map<String, Map<String, TrinketInventory>> inventory = trinketComponent.get().getInventory();
            Map<String, TrinketInventory> back = inventory.get("chest");
            if (back == null)
                  return;

            TrinketInventory slots = back.get("back");
            if (slots == null)
                  return;

            slots.setItem(0, stack);
      }

      public static ItemStack getBackStack(BackData backData, ItemStack stack) {
            Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(backData.owner);
            if (trinketComponent.isEmpty())
                  return stack;

            Map<String, Map<String, TrinketInventory>> inventory = trinketComponent.get().getInventory();
            Map<String, TrinketInventory> back = inventory.get("chest");
            if (back == null || back.isEmpty())
                  return stack;

            TrinketInventory slots = back.get("back");
            if (slots == null)
                  return stack;

            ItemStack stackInSlot = slots.getItem(0);
            if (stackInSlot != stack)
                  backData.update(stackInSlot);

            return stackInSlot;
      }

      public static boolean backSlotDisabled(LivingEntity entity) {
            return TrinketsApi.getTrinketComponent(entity).stream().anyMatch(in -> in.isEquipped(
                        wornStack -> Constants.DISABLES_BACK_SLOT.contains(wornStack.getItem())));
      }
}
