package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.ServerSave;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerLifecycleEvent implements ServerLifecycleEvents.ServerStarted, ServerLifecycleEvents.ServerStopping {
      @Override
      public void onServerStarted(MinecraftServer server) {
            ServerSave.updateSave(server);
      }

      @Override
      public void onServerStopping(MinecraftServer server) {
            ServerSave.updateSave(server);
      }
}
