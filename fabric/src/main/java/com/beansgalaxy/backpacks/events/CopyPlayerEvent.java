package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;

public class CopyPlayerEvent implements ServerPlayerEvents.CopyFrom {
      @Override
      public void copyFromPlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
            BackSlot oldBackSlot = BackSlot.get(oldPlayer);
            BackSlot newBackSlot = BackSlot.get(newPlayer);
            newBackSlot.replaceWith(oldBackSlot);
      }
}
