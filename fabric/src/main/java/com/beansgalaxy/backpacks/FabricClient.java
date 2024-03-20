package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.*;
import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
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
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.UUID;

public class FabricClient implements ClientModInitializer {

      @Override
      public void onInitializeClient() {
            NetworkPackages.registerS2CPackets();

            EntityModelLayerRegistry.registerModelLayer(RendererHelper.POT_MODEL, PotModel::getTexturedModelData);
            EntityModelLayerRegistry.registerModelLayer(RendererHelper.CAULDRON_MODEL, CauldronModel::createBodyLayer);
            EntityModelLayerRegistry.registerModelLayer(RendererHelper.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            EntityModelLayerRegistry.registerModelLayer(RendererHelper.PACK_WINGS_MODEL, BackpackWingsModel::createBodyLayer);
            LivingEntityFeatureRendererRegistrationCallback.EVENT.register(new AppendModelLayers());

            EntityRendererRegistry.register(FabricMain.BACKPACK_ENTITY, BackpackRenderer::new);
            EntityRendererRegistry.register(FabricMain.ENDER_ENTITY, BackpackRenderer::new);
            EntityRendererRegistry.register(FabricMain.WINGED_ENTITY, BackpackRenderer::new);
            MenuScreens.register(FabricMain.BACKPACK_MENU, BackpackScreen::new);

            KeyBindingHelper.registerKeyBinding(KeyPress.INSTANCE.ACTION_KEY);
            ClientEntityEvents.ENTITY_LOAD.register(new LoadEntityEvent());
            ColorProviderRegistry.ITEM.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), FabricMain.LEATHER_BACKPACK);
            ColorProviderRegistry.ITEM.register((stack, layer) -> switch (layer) {
                  case 0 -> WingedBackpack.shiftColorLayer0(((WingedBackpack) stack.getItem()).getColor(stack));
                  case 2 -> WingedBackpack.shiftColorLayer2(((WingedBackpack) stack.getItem()).getColor(stack));
                  default -> 0xFFFFFF;
            }, FabricMain.WINGED_BACKPACK);

            ItemProperties.register(Services.REGISTRY.getLeather(),
                        new ResourceLocation("is_yellow"), RendererHelper.IS_YELLOW_ITEM_PREDICATE);
            ItemProperties.register(Services.REGISTRY.getWinged(),
                        new ResourceLocation("is_yellow"), RendererHelper.IS_YELLOW_ITEM_PREDICATE);

            ModelLoadingPlugin.register(new LoadItemModels());

            if (Services.COMPAT.isModLoaded(CompatHelper.TRINKETS))
                  TrinketsRegistry.register();

            ClientPlayNetworking.registerGlobalReceiver(FabricMain.INITIAL_SYNC, (client, handler, buf, responseSender) -> {
                  UUID uuid = buf.readUUID();
                  CompoundTag trim = buf.readNbt();
                  NonNullList<ItemStack> itemStacks = NonNullList.create();
                  BackpackInventory.readStackNbt(buf.readNbt(), itemStacks);
                  String string = buf.readUtf();
                  MutableComponent playerName = Component.Serializer.fromJson(string);

                  EnderStorage.Data computed = ServerSave.MAPPED_ENDER_DATA.computeIfAbsent(uuid, in -> new EnderStorage.Data());
                  computed.setPlayerName(playerName).setTrim(trim).setItemStacks(itemStacks);

            });
      }
}
