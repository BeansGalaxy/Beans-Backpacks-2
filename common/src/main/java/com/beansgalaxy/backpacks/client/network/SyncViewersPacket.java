package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SyncViewersPacket {
      public static void receiveAtClient(int id, byte viewers) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(id);
            if (entity instanceof BackpackEntity backpackEntity)
                  backpackEntity.viewable.viewers = viewers;
            else if (entity instanceof Player player) {
                  BackData.get(player).backpackInventory.getViewable().viewers = viewers;
            }
      }
}
