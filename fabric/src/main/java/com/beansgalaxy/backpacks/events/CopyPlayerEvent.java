package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.clientbound.SendBackInventory;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;

public class CopyPlayerEvent implements ServerPlayerEvents.CopyFrom {
      @Override
      public void copyFromPlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
            if (alive || ConfigHelper.keepBackSlot(oldPlayer.level())) {
                  BackData oldBackData = BackData.get(oldPlayer);
                  BackData newBackData = BackData.get(newPlayer);
                  oldBackData.copyTo(newBackData);
                  SendBackInventory.send(newPlayer);
            }
      }
}
