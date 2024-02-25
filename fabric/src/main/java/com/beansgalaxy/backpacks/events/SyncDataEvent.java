package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.packages.ConfigureKeys2C;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;

public class SyncDataEvent implements ServerLifecycleEvents.SyncDataPackContents {
      @Override
      public void onSyncDataPackContents(ServerPlayer player, boolean joined) {
            ConfigureKeys2C.S2C(player);
            Constants.LOG.info("Syncing {} data to \"{}\"", Constants.MOD_ID, player.getDisplayName().getString());
      }
}
