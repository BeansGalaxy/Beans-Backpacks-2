package com.beansgalaxy.backpacks.events;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ElytraFlightEvent implements EntityElytraEvents.Custom {
      @Override
      public boolean useCustomElytra(LivingEntity entity, boolean tickElytra) {

            if (entity instanceof Player player)
            {
                  return ElytraEvent.doesFlyFall(tickElytra, player);
            }
            return false;
      }

}
