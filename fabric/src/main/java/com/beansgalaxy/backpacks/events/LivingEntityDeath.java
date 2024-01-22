package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.core.BackData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class LivingEntityDeath implements ServerLivingEntityEvents.AfterDeath {
      @Override
      public void afterDeath(LivingEntity entity, DamageSource damageSource) {
            if (entity instanceof Player player && !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                  BackData.get(player).drop();
      }
}
