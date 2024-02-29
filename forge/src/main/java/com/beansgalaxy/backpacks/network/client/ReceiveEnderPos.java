package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.function.Supplier;

public class ReceiveEnderPos {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(ReceiveEnderPos.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(ReceiveEnderPos::encode).decoder(ReceiveEnderPos::new).consumerMainThread(ReceiveEnderPos::handle).add();
      }

      final HashSet<ServerSave.EnderLocation> enderLocations;

      public ReceiveEnderPos(HashSet<ServerSave.EnderLocation> enderLocations) {
            this.enderLocations = enderLocations;
      }

      public ReceiveEnderPos(FriendlyByteBuf buf) {
            enderLocations = new HashSet<>();
            for (int i = buf.readInt(); i > 0; i--)
                  enderLocations.add(new ServerSave.EnderLocation(buf));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeInt(enderLocations.size());
            for (ServerSave.EnderLocation location : enderLocations)
                  location.writeBuf(buf);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            CommonAtClient.receiveEnderPos(enderLocations);
      }
}
