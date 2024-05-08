package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.CommonClass;
import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.IConfig;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrinketsRegistry {
      public static void register() {
            Trinket trinket = new Trinket() {
                  @Override
                  public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        CommonClass.test();
                        return TrinketEnums.DropRule.KEEP;
                  }

                  @Override
                  public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        boolean b = Trinket.super.canEquip(stack, slot, entity) && slot.index() == 0;
                        boolean b1 = entity instanceof Player player && BackData.get(player).mayEquip(stack);
                        return b && !b1 && stack.getCount() > 1;

                  }

                  @Override
                  public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player)
                              return BackData.get(player).backSlot.mayPickup(player);

                        return Trinket.super.canUnequip(stack, slot, entity);
                  }

                  @Override
                  public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player && slot.index() == 0)
                              BackData.get(player).update(stack);

                        Trinket.super.onEquip(stack, slot, entity);
                  }

                  @Override
                  public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                        if (entity instanceof Player player && slot.index() == 0)
                              BackData.get(player).update(ItemStack.EMPTY);

                        Trinket.super.onUnequip(stack, slot, entity);
                  }
            };

            for (Kind kind: Kind.values())
                  TrinketsApi.registerTrinket(kind.getItem(), trinket);

            if (!Services.COMPAT.isModLoaded(CompatHelper.ELYTRA_SLOT))
                  TrinketsApi.registerTrinket(Items.ELYTRA.asItem(), trinket);

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

            return slots.getItem(0);
      }

      public static List<ItemStack> backSlotDisabled(LivingEntity entity) {
            Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(entity);
            return trinketComponent.map(component -> component.getAllEquipped().stream().map((ref -> {
                  ItemStack item = ref.getB();
                  if (IConfig.elytraOrDisables(item.getItem()))
                        return item;
                  return ItemStack.EMPTY;
            })).toList()).orElse(List.of());
      }

      public static boolean isBackSlot(Slot slot) {
            if (slot instanceof TrinketSlot trinket) {
                  SlotType slotType = trinket.getType();
                  boolean isChest = slotType.getGroup().equals("chest");
                  boolean isBack = slotType.getName().equals("back");
                  return isBack && isChest;

            }
            return false;
      }
}
