package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class ConfigureTraits implements Packet2C{

      final Map<String, CompoundTag> map;

      public ConfigureTraits(FriendlyByteBuf buf) {
            this.map = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readNbt);
      }

      public ConfigureTraits(HashMap<String, Traits> traitsMap) {
            Map<String, CompoundTag> map = new HashMap<>();
            for (String key : traitsMap.keySet()) {
                  Traits traits = Traits.get(key);
                  map.put(key, traits.toTag());
            }
            this.map = map;
      }

      public static void send(ServerPlayer player) {
            Services.NETWORK.send(Network2C.CONFIG_TRAITS_2C, new ConfigureTraits(Constants.TRAITS_MAP), player);
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.CONFIG_TRAITS_2C.debugMsgEncode();
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeNbt);
      }

      @Override
      public void handle() {
            Network2C.CONFIG_TRAITS_2C.debugMsgDecode();
            for (String key: map.keySet())
            {
                  CompoundTag tag = map.get(key);
                  Traits traits = new Traits(tag);

                  Traits.register(key, traits);
            }
      }
}
