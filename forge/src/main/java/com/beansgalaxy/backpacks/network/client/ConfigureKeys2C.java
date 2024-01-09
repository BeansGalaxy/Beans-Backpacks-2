package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Map;

public class ConfigureKeys2C {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(ConfigureKeys2C.class, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(ConfigureKeys2C::encode).decoder(ConfigureKeys2C::new).consumerMainThread(ConfigureKeys2C::handle).add();
      }

      final Map<String, CompoundTag> map;

      public ConfigureKeys2C(Map<String, CompoundTag> map) {
            this.map = map;
      }

      public ConfigureKeys2C(FriendlyByteBuf buf) {
            this(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readNbt));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeNbt);
      }

      public void handle(CustomPayloadEvent.Context context) {
            for (String key: map.keySet())
            {
                  CompoundTag tag = map.get(key);
                  Traits traits = new Traits(tag);

                  Traits.register(key, traits);
            }
      }
}
