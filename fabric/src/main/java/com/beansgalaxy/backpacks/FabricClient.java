package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.client.renderer.BackpackRenderer;
import com.beansgalaxy.backpacks.client.renderer.BackpackModel;
import com.beansgalaxy.backpacks.client.renderer.PotModel;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;

public class FabricClient implements ClientModInitializer {

      @Override
      public void onInitializeClient() {
            NetworkPackages.registerC2SPackets();

            EntityModelLayerRegistry.registerModelLayer(Constants.POT_MODEL, PotModel::getTexturedModelData);
            EntityModelLayerRegistry.registerModelLayer(Constants.BACKPACK_MODEL, BackpackModel::getTexturedModelData);
            EntityRendererRegistry.register(ENTITY, BackpackRenderer::new);
            MenuScreens.register(BACKPACK_MENU, BackpackScreen::new);


            ColorProviderRegistry.ITEM.register((stack, layer) ->
                        (layer != 1 ? ((DyableBackpack) stack.getItem()).getColor(stack) : 16777215), Services.REGISTRY.getLeather());
      }

      // REGISTERS MENUS
      public static final MenuType<BackpackMenu> BACKPACK_MENU = Registry.register(
                  BuiltInRegistries.MENU, new ResourceLocation(Constants.MOD_ID, "backpack_menu"),
                  new ExtendedScreenHandlerType<>(BackpackMenu::new));

      // REGISTERS ENTITY
      public static final EntityType<Entity> ENTITY = Registry.register(
                  BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Constants.MOD_ID, "backpack"),
                  FabricEntityTypeBuilder.create(MobCategory.MISC, BackpackEntity::new).build());
}
