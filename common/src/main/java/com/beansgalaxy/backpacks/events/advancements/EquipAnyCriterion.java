package com.beansgalaxy.backpacks.events.advancements;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.Optional;

public class EquipAnyCriterion extends SimpleCriterionTrigger<EquipAnyCriterion.Conditions> {

      @Override
      protected Conditions createInstance(JsonObject object, Optional var2, DeserializationContext var3) {
            Holder<Item> item = GsonHelper.getAsItem(object, "item");
            String key = GsonHelper.getAsString(object, "key", "");
            boolean isDyed = GsonHelper.getAsBoolean(object, "is_dyed", false);

            return new Conditions(item, key, isDyed);
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            Holder<Item> item;
            String key;
            Boolean isDyed;

            public Conditions(Holder<Item> item, String key, Boolean isDyed) {
                  super(Optional.empty());
                  this.item = item;
                  this.key = key;
                  this.isDyed = isDyed;
            }

            boolean requirementsMet(ItemStack backStack) {
                  if (isDyed) {
                        boolean backpackDyed =
                                    backStack.getItem() instanceof DyableBackpack dyableBackpack &&
                                    dyableBackpack.getColor(backStack) != Backpack.DEFAULT_COLOR;
                        return backpackDyed;
                  }

                  CompoundTag display = backStack.getTagElement("display");
                  if (!key.isEmpty() && display != null) {
                        String key = display.getString("key");
                        return Objects.equals(this.key, key);
                  }

                  if (item != null && !item.value().equals(Items.AIR))
                        return backStack.is(item);

                  return !backStack.isEmpty();
            }
      }

      public void trigger(ServerPlayer player) {
            BackData backSlot = BackData.get(player);
            ItemStack backStack = backSlot.getItem();
            trigger(player, conditions -> conditions.requirementsMet(backStack));
      }
}
