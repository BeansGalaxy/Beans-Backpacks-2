package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class ConfigureLists implements Packet2C {

      private final Map<String, String> map;

      public ConfigureLists(Map<String, String> map) {
            this.map = map;
      }

      public ConfigureLists(FriendlyByteBuf byteBuf) {
            this(byteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf));
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.CONFIG_LISTS_2C;
      }

      public static void send(ServerPlayer sender) {
            HashMap<String, String> map = new HashMap<>();

            map.put("disables_back_slot", Constants.writeList(Constants.DISABLES_BACK_SLOT));
            map.put("chestplate_disabled", Constants.writeList(Constants.CHESTPLATE_DISABLED));
            map.put("elytra_items", Constants.writeList(Constants.ELYTRA_ITEMS));
            map.put("blacklist_items", Constants.writeList(Constants.BLACKLIST_ITEMS));

            new ConfigureLists(map).send2C(sender);
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
      }

      public void handle() {
            getNetwork().debugMsgEncode();
            Constants.receiveItemLists(map);
      }
}
