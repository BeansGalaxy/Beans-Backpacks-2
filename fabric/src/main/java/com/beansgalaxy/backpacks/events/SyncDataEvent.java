package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.network.packages.ConfigureKeys2C;
import com.beansgalaxy.backpacks.platform.Services;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SyncDataEvent implements ServerLifecycleEvents.SyncDataPackContents {
      @Override
      public void onSyncDataPackContents(ServerPlayer player, boolean joined) {
            ConfigureKeys2C.S2C(player);
            Constants.LOG.info("Syncing {} data to \"{}\"", Constants.MOD_ID, player.getDisplayName().getString());
            if (joined) {
                  BackData backData = BackData.get(player);
                  Services.NETWORK.sendEnderLocations2C(player, backData);
            }
      }
}
