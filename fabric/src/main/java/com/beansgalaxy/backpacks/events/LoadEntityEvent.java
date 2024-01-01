package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class LoadEntityEvent implements ClientEntityEvents.Load {

      @Override
      public void onLoad(Entity entity, ClientLevel world) {
            if (entity instanceof RemotePlayer otherClientPlayer) {
                  FriendlyByteBuf buf = PacketByteBufs.create();
                  buf.writeUUID(otherClientPlayer.getUUID());
                  ClientPlayNetworking.send(NetworkPackages.CALL_BACK_SLOT_2S, buf);
            }
            if (entity instanceof LocalPlayer localPlayer)
                  ClientPlayNetworking.send(NetworkPackages.CALL_BACK_INV_2S, PacketByteBufs.create());
      }
}
