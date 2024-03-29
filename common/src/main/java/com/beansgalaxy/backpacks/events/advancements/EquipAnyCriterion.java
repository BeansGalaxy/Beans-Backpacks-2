package com.beansgalaxy.backpacks.events.advancements;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class EquipAnyCriterion extends SimpleCriterionTrigger<EquipAnyCriterion.Conditions> {
      public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID + "/equip_any");

      @Override
      protected Conditions createInstance(JsonObject object, ContextAwarePredicate contextAwarePredicate, DeserializationContext var3) {
            Item item = GsonHelper.getAsItem(object, "item");
            String key = GsonHelper.getAsString(object, "backpack_id", "");
            boolean isDyed = GsonHelper.getAsBoolean(object, "is_dyed", false);

            return new Conditions(item, key, isDyed, contextAwarePredicate);
      }

      @Override
      public ResourceLocation getId() {
            return ID;
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            Item item;
            String key;
            Boolean isDyed;

            public Conditions(Item item, String key, Boolean isDyed, ContextAwarePredicate contextAwarePredicate) {
                  super(ID, contextAwarePredicate);
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

                  CompoundTag display = backStack.getTag();
                  if (!Constants.isEmpty(key) && display != null) {
                        String key = display.getString("backpack_id");
                        return Objects.equals(this.key, key);
                  }

                  if (item != null && !item.equals(Items.AIR))
                        return backStack.is(item);

                  return !backStack.isEmpty();
            }
      }

      public void trigger(ServerPlayer player) {
            BackData backSlot = BackData.get(player);
            ItemStack backStack = backSlot.getStack();
            trigger(player, conditions -> conditions.requirementsMet(backStack));
      }
}
