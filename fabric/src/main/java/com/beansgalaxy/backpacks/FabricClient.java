package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class FabricClient implements ClientModInitializer {

      @Override
      public void onInitializeClient() {
            NetworkPackages.registerC2SPackets();

            ColorProviderRegistry.ITEM.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), Services.REGISTRY.getLeather());
      }
}
