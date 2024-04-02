package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class ConfigureLists2C {
      public static void S2C(ServerPlayer serverPlayer)
      {
            FriendlyByteBuf buf = PacketByteBufs.create();
            HashMap<String, String> map = new HashMap<>();

            map.put("disables_back_slot", Constants.writeList(Constants.DISABLES_BACK_SLOT));
            map.put("chestplate_disabled", Constants.writeList(Constants.CHESTPLATE_DISABLED));
            map.put("elytra_items", Constants.writeList(Constants.ELYTRA_ITEMS));
            map.put("blacklist_items", Constants.writeList(Constants.BLACKLIST_ITEMS));

            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
            ServerPlayNetworking.send(serverPlayer, NetworkPackages.CONFIG_LISTS_2C, buf);

      }
}
