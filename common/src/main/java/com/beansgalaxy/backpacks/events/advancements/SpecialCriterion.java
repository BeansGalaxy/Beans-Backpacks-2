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

public class SpecialCriterion extends SimpleCriterionTrigger<SpecialCriterion.Conditions> {
      static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID + "/special");

      @Override
      protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
            String special = GsonHelper.getAsString(jsonObject, "special", "");
            return new Conditions(special, contextAwarePredicate);
      }

      @Override
      public ResourceLocation getId() {
            return ID;
      }

      public static class Conditions extends AbstractCriterionTriggerInstance {
            Special special;

            public Conditions(String special, ContextAwarePredicate contextAwarePredicate) {
                  super(ID, contextAwarePredicate);
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
