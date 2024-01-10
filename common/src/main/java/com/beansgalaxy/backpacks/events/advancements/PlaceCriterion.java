package com.beansgalaxy.backpacks.events.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.Objects;
import java.util.Optional;

public class PlaceCriterion extends SimpleCriterionTrigger<PlaceCriterion.Conditions> {

      @Override
      protected Conditions createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
            String key = GsonHelper.getAsString(jsonObject, "key", "");
            return new Conditions(key);
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            private final String key;

            public Conditions(String key) {
                  super(Optional.empty());
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
