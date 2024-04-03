package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigureLists2C {
      public static void register(int i) {
            NetworkPackages.INSTANCE.messageBuilder(ConfigureLists2C.class, i, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(ConfigureLists2C::encode).decoder(ConfigureLists2C::new).consumerMainThread(ConfigureLists2C::handle).add();
      }

      private final Map<String, String> map;

      public ConfigureLists2C(Map<String, String> map) {
            this.map = map;
      }

      public ConfigureLists2C(FriendlyByteBuf byteBuf) {
            this(byteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
      }

      public void handle(Supplier<NetworkEvent.Context> context) {
            Constants.receiveItemLists(map);
      }
}
