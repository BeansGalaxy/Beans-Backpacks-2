package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.clientbound.ConfigureLists;
import com.beansgalaxy.backpacks.network.clientbound.ConfigureTraits;
import com.beansgalaxy.backpacks.platform.Services;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class SyncDataEvent implements ServerLifecycleEvents.SyncDataPackContents {
      @Override
      public void onSyncDataPackContents(ServerPlayer player, boolean joined) {
            sendTraits(player);
            sendLists(player);

            Constants.LOG.info("Syncing {} data to \"{}\"", Constants.MOD_ID, player.getDisplayName().getString());
            if (joined) {
                  BackData backData = BackData.get(player);
                  Services.NETWORK.sendEnderLocations2C(player, backData);
            }
      }

      private static void sendLists(ServerPlayer serverPlayer) {
            HashMap<String, String> map = new HashMap<>();

            map.put("disables_back_slot", Constants.writeList(Constants.DISABLES_BACK_SLOT));
            map.put("chestplate_disabled", Constants.writeList(Constants.CHESTPLATE_DISABLED));
            map.put("elytra_items", Constants.writeList(Constants.ELYTRA_ITEMS));
            map.put("blacklist_items", Constants.writeList(Constants.BLACKLIST_ITEMS));

            NetworkPackages.send(Network2C.CONFIG_LISTS_2C, new ConfigureLists(map), serverPlayer);
      }

      private static void sendTraits(ServerPlayer serverPlayer) {
            NetworkPackages.send(Network2C.CONFIG_TRAITS_2C, new ConfigureTraits(Constants.TRAITS_MAP), serverPlayer);
      }
}
