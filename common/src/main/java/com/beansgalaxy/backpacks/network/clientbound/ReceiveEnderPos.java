package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;

public class ReceiveEnderPos implements Packet2C {
      final HashSet<EnderStorage.PackagedLocation> enderLocations;

      public ReceiveEnderPos(BackData backData) {
            this.enderLocations = backData.getEnderLocations();
      }

      public ReceiveEnderPos(FriendlyByteBuf buf) {
            enderLocations = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--)
                  enderLocations.add(new EnderStorage.PackagedLocation(buf));
      }

      public static void send(ServerPlayer serverPlayer, BackData backData) {
            Services.NETWORK.send(Network2C.ENDER_POS_2C, new ReceiveEnderPos(backData), serverPlayer);
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.ENDER_POS_2C.debugMsgEncode();
            buf.writeInt(enderLocations.size());
            for (EnderStorage.PackagedLocation location : enderLocations)
                  location.writeBuf(buf);
      }

      @Override
      public void handle() {
            Network2C.ENDER_POS_2C.debugMsgDecode();
            CommonAtClient.receiveEnderPos(enderLocations);
      }
}
