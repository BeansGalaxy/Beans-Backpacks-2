package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.network.packages.ConfigBackpackData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class PlayerJoin implements ServerEntityEvents.Load {
      @Override
      public void onLoad(Entity entity, ServerLevel world) {
            if (entity instanceof ServerPlayer serverPlayer)
                  ConfigBackpackData.S2C(serverPlayer);
      }
}
