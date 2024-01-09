package com.beansgalaxy.backpacks.events.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

public class SpecialCriterion extends SimpleCriterionTrigger<SpecialCriterion.Conditions> {

      @Override
      protected Conditions createInstance(JsonObject object, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
            String special = GsonHelper.getAsString(object, "special", "");
            return new Conditions(special);
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            Special special;

            public Conditions(String special) {
                  super(Optional.empty());
                  this.special = Special.valueOf(special);
            }

            boolean requirementsMet(Special special) {
                  return this.special == special;
            }
      }

      public void trigger(ServerPlayer player, Special special) {
            trigger(player, conditions -> conditions.requirementsMet(special));
      }

      public enum Special {
            HOP,
            LAYERED,
            FILLED_LEATHER
      }
}
