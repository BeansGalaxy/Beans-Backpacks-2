package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.client.renderer.PotModel;
import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.BackpackScreen;
import com.beansgalaxy.backpacks.events.AppendModelLayers;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.events.LoadEntityEvent;
import com.beansgalaxy.backpacks.events.LoadItemModels;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class FabricClient implements ClientModInitializer {

      @Override
      public void onInitializeClient() {
            NetworkPackages.registerS2CPackets();

            EntityModelLayerRegistry.registerModelLayer(RendererHelper.POT_MODEL, PotModel::getTexturedModelData);
            EntityModelLayerRegistry.registerModelLayer(RendererHelper.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            LivingEntityFeatureRendererRegistrationCallback.EVENT.register(new AppendModelLayers());

            EntityRendererRegistry.register(FabricMain.BACKPACK_ENTITY, BackpackRenderer::new);
            EntityRendererRegistry.register(FabricMain.ENDER_ENTITY, BackpackRenderer::new);
            MenuScreens.register(FabricMain.BACKPACK_MENU, BackpackScreen::new);

            KeyBindingHelper.registerKeyBinding(KeyPress.INSTANCE.ACTION_KEY);
            ClientEntityEvents.ENTITY_LOAD.register(new LoadEntityEvent());
            ColorProviderRegistry.ITEM.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), FabricMain.LEATHER_BACKPACK);
            ColorProviderRegistry.ITEM.register((stack, layer) ->
                    (layer != 1 ? WingedBackpack.shiftColor(((WingedBackpack) stack.getItem()).getColor(stack)) : 16777215), FabricMain.WINGED_BACKPACK);

            ModelLoadingPlugin.register(new LoadItemModels());

            if (Services.COMPAT.isModLoaded(CompatHelper.TRINKETS))
                  TrinketsRegistry.register();

            ClientPlayNetworking.registerGlobalReceiver(FabricMain.INITIAL_SYNC, (client, handler, buf, responseSender) -> {
                  UUID uuid = buf.readUUID();
                  CompoundTag trim = buf.readNbt();
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  BackpackInventory.readStackNbt(buf.readNbt(), itemStacks);
                  ServerSave.MAPPED_ENDER_DATA.put(uuid, new ServerSave.EnderData(itemStacks, trim));
            });
      }
}
