package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.client.ConfigureKeys2C;
import com.beansgalaxy.backpacks.network.client.SyncBackInventory2C;
import com.beansgalaxy.backpacks.network.client.SyncBackSlot2C;
import com.beansgalaxy.backpacks.network.client.SyncViewersPacket2C;
import com.beansgalaxy.backpacks.network.packages.CallBackInventory2S;
import com.beansgalaxy.backpacks.network.packages.CallBackSlot2S;
import com.beansgalaxy.backpacks.network.packages.InstantPlace2S;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket2S;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkPackages {
      private static final String PROTOCOL = "1";
      public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
                  new ResourceLocation(Constants.MOD_ID, "main"),
                  () -> PROTOCOL,
                  PROTOCOL::equals,
                  PROTOCOL::equals);

      public static void register() {
            int index = 0;
            SprintKeyPacket2S.register(index++);
            InstantPlace2S.register(index++);
            SyncViewersPacket2C.register(index++);
            SyncBackSlot2C.register(index++);
            CallBackSlot2S.register(index++);
            SyncBackInventory2C.register(index++);
            CallBackInventory2S.register(index++);
            ConfigureKeys2C.register(index);
      }

      public static void C2S(Object mgs) {
            INSTANCE.send(PacketDistributor.SERVER.noArg(), mgs);
      }

      public static void S2C(Object msg, ServerPlayer player) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
      }

      public static void S2All(Object msg) {
            INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
      }

}
