package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.CommonClass;
import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.*;
import com.beansgalaxy.backpacks.client.renderer.features.BackFeature;
import com.beansgalaxy.backpacks.client.renderer.features.BackpackFeature;
import com.beansgalaxy.backpacks.client.renderer.features.PotFeature;
import com.beansgalaxy.backpacks.entity.BackpackScreen;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
      @SubscribeEvent
      public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                  MenuScreens.register(ForgeMain.MENU.get(), BackpackScreen::new);
            });
      }

      @SubscribeEvent
      public static void appendLayers(EntityRenderersEvent.AddLayers event) {
            for (String skin : event.getSkins()) {
                  LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
                          renderer = event.getSkin(skin);

                  if (renderer == null)
                        continue;

                  renderer.addLayer(new BackFeature<>(
                          renderer, event.getEntityModels(), event.getContext().getModelManager()
                  ));
            }
      }

      @SubscribeEvent
      public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ForgeMain.ENTITY.get(), BackpackRenderer::new);
            event.registerEntityRenderer(ForgeMain.ENDER_ENTITY.get(), BackpackRenderer::new);
      }

      @SubscribeEvent
      public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RendererHelper.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            event.registerLayerDefinition(RendererHelper.POT_MODEL, PotModel::getTexturedModelData);
      }

      @SubscribeEvent
      public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), ForgeMain.LEATHER_BACKPACK.get());
            event.register((stack, layer) ->
                        (layer != 1 ? WingedBackpack.shiftColor(((WingedBackpack) stack.getItem()).getColor(stack)) : 16777215), ForgeMain.WINGED_BACKPACK.get());
      }

      @SubscribeEvent
      public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(KeyPress.INSTANCE.ACTION_KEY);
      }

      @SubscribeEvent
      public static void loadItemModels(ModelEvent.RegisterAdditional event) {
            for(ModelResourceLocation modelId : ModelResources.get())
                  event.register(modelId);
      }

}
