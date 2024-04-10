package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.ForgeMain;
import com.beansgalaxy.backpacks.client.renderer.RenderHelper;
import com.beansgalaxy.backpacks.client.renderer.*;
import com.beansgalaxy.backpacks.client.renderer.features.BackFeature;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.models.BackpackWingsModel;
import com.beansgalaxy.backpacks.client.renderer.models.CauldronModel;
import com.beansgalaxy.backpacks.client.renderer.models.PotModel;
import com.beansgalaxy.backpacks.inventory.BackpackTooltip;
import com.beansgalaxy.backpacks.inventory.SpecialTooltip;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
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
            event.registerEntityRenderer(ForgeMain.ENTITY_GENERAL.get(), BackpackRenderer::new);
            event.registerEntityRenderer(ForgeMain.ENTITY_ENDER.get(), BackpackRenderer::new);
            event.registerEntityRenderer(ForgeMain.ENTITY_WINGED.get(), BackpackRenderer::new);
      }

      @SubscribeEvent
      public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RenderHelper.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            event.registerLayerDefinition(RenderHelper.PACK_WINGS_MODEL, BackpackWingsModel::createBodyLayer);
            event.registerLayerDefinition(RenderHelper.POT_MODEL, PotModel::getTexturedModelData);
            event.registerLayerDefinition(RenderHelper.CAULDRON_MODEL, CauldronModel::createBodyLayer);
      }

      @SubscribeEvent
      public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), ForgeMain.LEATHER_BACKPACK.get());
            event.register((stack, layer) -> switch (layer) {
                  case 0 -> WingedBackpack.shiftLayer0(((WingedBackpack) stack.getItem()).getColor(stack));
                  case 2 -> WingedBackpack.shiftLayer2(((WingedBackpack) stack.getItem()).getColor(stack));
                  default -> 0xFFFFFF; }, ForgeMain.WINGED_BACKPACK.get());
            ItemProperties.register(Services.REGISTRY.getLeather(),
                        new ResourceLocation(Constants.MOD_ID, "is_yellow"), RenderHelper.IS_YELLOW_ITEM_PREDICATE);
            ItemProperties.register(Services.REGISTRY.getWinged(),
                        new ResourceLocation(Constants.MOD_ID, "is_yellow"), RenderHelper.IS_YELLOW_ITEM_PREDICATE);
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

      @SubscribeEvent
      public static void tooltipImageEvent(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpecialTooltip.Pot.class, ClientSpecialTooltip.Pot::new);
            event.register(SpecialTooltip.Cauldron.class, ClientSpecialTooltip.Cauldron::new);
            event.register(BackpackTooltip.class, ClientBackpackTooltip::new);
      }

}
