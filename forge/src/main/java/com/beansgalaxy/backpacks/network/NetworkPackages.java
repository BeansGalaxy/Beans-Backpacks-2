package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.packages.CallBackSlotC2S;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacketC2S;
import com.beansgalaxy.backpacks.network.packages.SyncBackSlotS2C;
import com.beansgalaxy.backpacks.network.packages.SyncViewersPacketS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class NetworkPackages {
      private static SimpleChannel INSTANCE = ChannelBuilder.named(
                  new ResourceLocation(Constants.MOD_ID, "main"))
                  .serverAcceptedVersions(((status, version) -> true))
                  .clientAcceptedVersions(((status, version) -> true))
                  .networkProtocolVersion(1)
                  .simpleChannel();

      public static void register() {
            INSTANCE.messageBuilder(SprintKeyPacketC2S.class, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(SprintKeyPacketC2S::encode).decoder(SprintKeyPacketC2S::new).consumerMainThread(SprintKeyPacketC2S::handle).add();

            INSTANCE.messageBuilder(SyncViewersPacketS2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncViewersPacketS2C::encode).decoder(SyncViewersPacketS2C::new).consumerMainThread(SyncViewersPacketS2C::handle).add();

            INSTANCE.messageBuilder(SyncBackSlotS2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(SyncBackSlotS2C::encode).decoder(SyncBackSlotS2C::new).consumerMainThread(SyncBackSlotS2C::handle).add();

            INSTANCE.messageBuilder(CallBackSlotC2S.class, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(CallBackSlotC2S::encode).decoder(CallBackSlotC2S::new).consumerMainThread(CallBackSlotC2S::handle).add();
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
