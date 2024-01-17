package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.core.BackData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;

public class CopyPlayerEvent implements ServerPlayerEvents.CopyFrom {
      @Override
      public void copyFromPlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
            BackData.get(oldPlayer).copyTo(BackData.get(newPlayer));
      }
}
