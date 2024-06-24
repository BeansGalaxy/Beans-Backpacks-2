package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.clientbound.Packet2C;
import com.beansgalaxy.backpacks.network.serverbound.Packet2S;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
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
            int i = 0;
            for (Network2S value : Network2S.values())
                  register2S(value.packet, i++);

            for (Network2C value : Network2C.values())
                  register2C(value.packet, i++);
      }

      public static <T extends Packet2S> void register2S(Network2S.Packet<T> packet, int i) {
            NetworkPackages.INSTANCE.messageBuilder(packet.type(), i, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(packet.encoder())
                        .decoder(packet.decoder())
                        .consumerMainThread((msg, ctx) -> {
                              ServerPlayer sender = ctx.get().getSender();
                              msg.handle(sender);
                        }).add();
      }

      public static <T extends Packet2C> void register2C(Network2C.Packet<T> packet, int i) {
            NetworkPackages.INSTANCE.messageBuilder(packet.type(), i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(packet.encoder())
                        .decoder(packet.decoder())
                        .consumerMainThread((msg, ctx) -> {
                              msg.handle();
                        }).add();
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
