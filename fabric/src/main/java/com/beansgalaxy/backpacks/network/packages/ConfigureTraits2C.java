package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureTraits2C {
      public static void S2C(ServerPlayer serverPlayer)
      {
            FriendlyByteBuf buf = PacketByteBufs.create();

            buf.writeMap(Constants.TRAITS_MAP, FriendlyByteBuf::writeUtf,
                        (b, data) -> b.writeNbt(data.toTag()));

            ServerPlayNetworking.send(serverPlayer, NetworkPackages.CONFIG_TRAITS_2C, buf);

      }
}
