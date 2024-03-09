package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.client.renderer.features.CauldronFeature;
import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.awt.*;

public class FabricCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return FabricLoader.getInstance().isModLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(BackData backData, ItemStack stack) {
            if (isModLoaded(TRINKETS))
                  TrinketsRegistry.setBackStack(stack, backData);
      }

      @Override
      public ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.getBackStack(backData, defaultItem);
            return defaultItem;
      }

      @Override
      public boolean backSlotDisabled(Player owner) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.backSlotDisabled(owner);
            return false;
      }

      @Override
      public CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas) {
            TextureAtlasSprite sprite = FluidStackHooks.getStillTexture(fluid);
            int color = FluidStackHooks.getColor(fluid);
            return new CauldronInventory.FluidAttributes(sprite, new Color(color));
      }
}
