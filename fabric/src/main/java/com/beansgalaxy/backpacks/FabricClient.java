package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.client.renderer.PotModel;
import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.entity.BackpackScreen;
import com.beansgalaxy.backpacks.events.AppendModelLayers;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.events.LoadEntityEvent;
import com.beansgalaxy.backpacks.events.LoadItemModels;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricClient implements ClientModInitializer {

      @Override
      public void onInitializeClient() {
            NetworkPackages.registerS2CPackets();

            EntityModelLayerRegistry.registerModelLayer(Constants.POT_MODEL, PotModel::getTexturedModelData);
            EntityModelLayerRegistry.registerModelLayer(Constants.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            LivingEntityFeatureRendererRegistrationCallback.EVENT.register(new AppendModelLayers());

            EntityRendererRegistry.register(FabricMain.BACKPACK_ENTITY, BackpackRenderer::new);
            MenuScreens.register(FabricMain.BACKPACK_MENU, BackpackScreen::new);

            KeyBindingHelper.registerKeyBinding(KeyPress.INSTANCE.ACTION_KEY);
            ClientEntityEvents.ENTITY_LOAD.register(new LoadEntityEvent());
            ColorProviderRegistry.ITEM.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), FabricMain.LEATHER_BACKPACK);

            ModelLoadingPlugin.register(new LoadItemModels());

            if (Services.COMPAT.isModLoaded(CompatHelper.TRINKETS))
                  TrinketsRegistry.register();
      }
}
