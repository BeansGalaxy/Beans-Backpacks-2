package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.client.RendererHelper;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.client.renderer.PotModel;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
      public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RendererHelper.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            event.registerLayerDefinition(RendererHelper.POT_MODEL, PotModel::getTexturedModelData);
      }

      @SubscribeEvent
      public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ForgeMain.ENTITY.get(), BackpackRenderer::new);
      }

      @SubscribeEvent
      public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), ForgeMain.LEATHER_BACKPACK.get());
      }

      @SubscribeEvent
      public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(KeyPress.INSTANCE.ACTION_KEY);
      }

      @SubscribeEvent
      public static void registerCreativeTab(BuildCreativeModeTabContentsEvent event) {

      }

}
