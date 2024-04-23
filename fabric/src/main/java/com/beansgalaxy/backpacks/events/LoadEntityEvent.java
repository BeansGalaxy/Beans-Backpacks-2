package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.Network2S;
import com.beansgalaxy.backpacks.network.serverbound.CallBackInventory;
import com.beansgalaxy.backpacks.network.serverbound.CallBackSlot;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class LoadEntityEvent implements ClientEntityEvents.Load {

      @Override
      public void onLoad(Entity entity, ClientLevel world) {
            if (entity instanceof RemotePlayer otherClientPlayer) {
                  UUID uuid = otherClientPlayer.getUUID();
                  CallBackSlot.send(uuid);
            }
            if (entity instanceof LocalPlayer localPlayer) {
                  UUID uuid = localPlayer.getUUID();
                  CallBackInventory.send(uuid);
            }
      }
}
