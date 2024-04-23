package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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
