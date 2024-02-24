package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.ServerSave;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStartEvent implements ServerLifecycleEvents.ServerStarted {
      @Override
      public void onServerStarted(MinecraftServer server) {
            ServerSave.getServerState(server);
      }
}
