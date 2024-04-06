package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class LivingEntityDeath implements ServerLivingEntityEvents.AllowDeath {

      @Override
      public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
            if (entity instanceof Player player && !ConfigHelper.keepBackSlot(player.level()))
                  BackData.get(player).drop();
            return true;
      }
}
