package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;

public class ConfigureLists implements Packet2C {

      private final Map<String, String> map;

      public ConfigureLists(Map<String, String> map) {
            this.map = map;
      }

      public ConfigureLists(FriendlyByteBuf byteBuf) {
            this(byteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf));
      }

      public void encode(FriendlyByteBuf buf) {
            Network2C.CONFIG_LISTS_2C.debugMsgDecode();
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
      }

      public void handle() {
            Network2C.CONFIG_LISTS_2C.debugMsgEncode();
            Constants.receiveItemLists(map);
      }
}
