package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.client.SyncBackInventory2C;
import com.beansgalaxy.backpacks.network.client.SyncBackSlotS2C;
import com.beansgalaxy.backpacks.network.client.SyncViewersPacketS2C;
import com.beansgalaxy.backpacks.network.packages.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class NetworkPackages {
      public static SimpleChannel INSTANCE = ChannelBuilder.named(
                  new ResourceLocation(Constants.MOD_ID, "main"))
                  .serverAcceptedVersions(((status, version) -> true))
                  .clientAcceptedVersions(((status, version) -> true))
                  .networkProtocolVersion(1)
                  .simpleChannel();

      public static void register() {
            SprintKeyPacketC2S.register();
            SyncViewersPacketS2C.register();
            SyncBackSlotS2C.register();
            CallBackSlotC2S.register();
            SyncBackInventory2C.register();
            CallBackInventory2C.register();
      }

      public static void C2S(Object mgs) {
            INSTANCE.send(mgs, PacketDistributor.SERVER.noArg());
      }

      public static void S2C(Object msg, ServerPlayer player) {
            INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
      }

      public static void S2All(Object msg) {
            INSTANCE.send(msg, PacketDistributor.ALL.noArg());
      }

}
