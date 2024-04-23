package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.Network2C;
import com.beansgalaxy.backpacks.network.clientbound.ConfigureLists;
import com.beansgalaxy.backpacks.network.clientbound.ConfigureTraits;
import com.beansgalaxy.backpacks.network.clientbound.ReceiveEnderPos;
import com.beansgalaxy.backpacks.platform.Services;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class SyncDataEvent implements ServerLifecycleEvents.SyncDataPackContents {
      @Override
      public void onSyncDataPackContents(ServerPlayer player, boolean joined) {
            ConfigureTraits.send(player);
            ConfigureLists.send(player);

            Constants.LOG.info("Syncing {} data to \"{}\"", Constants.MOD_ID, player.getDisplayName().getString());
            if (joined) {
                  BackData backData = BackData.get(player);
                  ReceiveEnderPos.send(player, backData);
            }
      }
}
