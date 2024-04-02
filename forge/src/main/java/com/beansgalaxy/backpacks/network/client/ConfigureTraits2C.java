package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigureTraits2C {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(ConfigureTraits2C.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(ConfigureTraits2C::encode).decoder(ConfigureTraits2C::new).consumerMainThread(ConfigureTraits2C::handle).add();
      }

      final Map<String, CompoundTag> map;

      public ConfigureTraits2C(Map<String, CompoundTag> map) {
            this.map = map;
      }

      public ConfigureTraits2C(FriendlyByteBuf buf) {
            this(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readNbt));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeNbt);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            for (String key: map.keySet())
            {
                  CompoundTag tag = map.get(key);
                  Traits traits = new Traits(tag);

                  Traits.register(key, traits);
            }
      }
}
