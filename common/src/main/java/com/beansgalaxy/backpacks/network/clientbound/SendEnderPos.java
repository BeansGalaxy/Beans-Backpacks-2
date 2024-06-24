package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;

public class SendEnderPos implements Packet2C {
      final HashSet<EnderStorage.PackagedLocation> enderLocations;

      private SendEnderPos(BackData backData) {
            this.enderLocations = backData.getEnderLocations();
      }

      public SendEnderPos(FriendlyByteBuf buf) {
            enderLocations = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--)
                  enderLocations.add(new EnderStorage.PackagedLocation(buf));
      }

      public static void send(ServerPlayer serverPlayer, BackData backData) {
            new SendEnderPos(backData).send2C(serverPlayer);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.ENDER_POS_2C;
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(enderLocations.size());
            for (EnderStorage.PackagedLocation location : enderLocations)
                  location.writeBuf(buf);
      }

      @Override
      public void handle() {
            getNetwork().debugMsgDecode();
            CommonAtClient.receiveEnderPos(enderLocations);
      }
}
