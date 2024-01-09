package com.beansgalaxy.backpacks.events.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PlaceCriterion extends SimpleCriterionTrigger<PlaceCriterion.Conditions> {

      @Override
      protected Conditions createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {

            return new Conditions();
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            public Conditions() {
                  super(Optional.empty());
            }
      }

      public void trigger(ServerPlayer player) {
            trigger(player, conditions -> true);
      }
}
