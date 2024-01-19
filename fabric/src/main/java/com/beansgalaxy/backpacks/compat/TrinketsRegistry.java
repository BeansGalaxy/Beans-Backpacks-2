package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import dev.emi.trinkets.api.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TrinketsRegistry {
      public static int chestBackIndex = -1;

      public static void register() {
            Trinket trinket = new Trinket() {
                  @Override
                  public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player) {
                              if (chestBackIndex < 0)
                                    chestBackIndex = slot.index();
                              else {
                                    ItemStack trinketStack = slot.inventory().getItem(chestBackIndex);
                                    BackData backSlot = BackData.get(player);
                                    if (trinketStack != backSlot.getStack())
                                          backSlot.update(trinketStack);
                              }
                        }
                  }

                  @Override
                  public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player) {
                              ItemStack trinketsStack = slot.inventory().getItem(slot.index());
                              BackData.get(player).update(trinketsStack);
                        }
                  }

                  @Override
                  public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player) {
                              ItemStack trinketsStack = slot.inventory().getItem(slot.index());
                              BackData.get(player).update(trinketsStack);
                        }
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

            TrinketsApi.registerTrinket(Items.ELYTRA.asItem(), trinket);
      }

      public static void setBackStack(ItemStack stack, Player player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent -> {
                        trinketComponent.forEach(((slotReference, stack1) -> {
                              SlotType slotType = slotReference.inventory().getSlotType();
                              String name = slotType.getName();
                              String group = slotType.getGroup();
                              if (Objects.equals(name, "back") && Objects.equals(group, "chest")) {
                                    int index = slotReference.index();
                                    chestBackIndex = index;
                                    slotReference.inventory().setItem(index, stack);
                              }
                        }));
            });
      }

      public static ItemStack getBackStack(Player player, ItemStack stack) {
            AtomicReference<ItemStack> backStack = new AtomicReference<>(stack);
            TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent -> {
                  trinketComponent.forEach(((slotReference, stack1) -> {
                        TrinketInventory inventory = slotReference.inventory();
                        SlotType slotType = inventory.getSlotType();
                        String name = slotType.getName();
                        String group = slotType.getGroup();
                        if (Objects.equals(name, "back") && Objects.equals(group, "chest")) {
                              int index = slotReference.index();
                              chestBackIndex = index;
                              backStack.set(inventory.getItem(index));
                        }
                  }));
            });
            return backStack.get();
      }
}
