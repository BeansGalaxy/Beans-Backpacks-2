package com.beansgalaxy.backpacks.events.advancements;

import com.beansgalaxy.backpacks.Constants;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.Objects;

public class PlaceCriterion extends SimpleCriterionTrigger<PlaceCriterion.Conditions> {
      static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID + "/place");

      @Override
      protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext var3) {
            String key = GsonHelper.getAsString(jsonObject, "key", "");
            return new Conditions(key, contextAwarePredicate);
      }

      @Override
      public ResourceLocation getId() {
            return ID;
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            private final String key;

            public Conditions(String key, ContextAwarePredicate contextAwarePredicate) {
                  super(ID, contextAwarePredicate);
                  this.key = key;
            }

            boolean requirementsMet(String key) {
                  if (this.key == null || this.key.isEmpty())
                        return true;

                  return Objects.equals(this.key, key);
            }
      }

      public void trigger(ServerPlayer player, String key) {
            trigger(player, conditions -> conditions.requirementsMet(key));
      }
}
