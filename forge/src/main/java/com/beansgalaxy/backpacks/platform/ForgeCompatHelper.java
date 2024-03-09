package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidHandlerBlockEntity;
import net.minecraftforge.fml.ModList;

import java.awt.*;

public class ForgeCompatHelper implements CompatHelper {

      @Override
      public boolean isModLoaded(String namespace) {
            return ModList.get().isLoaded(namespace);
      }

      @Override
      public void setBackSlotItem(BackData backData, ItemStack stack) {
            if (isModLoaded(CURIOS))
                  CurioRegistry.setBackStack(stack, backData.owner);
      }

      @Override
      public ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.getBackStackItem(backData, defaultItem);
            return defaultItem;
      }

      @Override
      public boolean backSlotDisabled(Player owner) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.backSlotDisables(owner);
            return false;
      }

      @Override
      public CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas) {
            IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluid);
            TextureAtlasSprite sprite = blocksAtlas.getSprite(attributes.getStillTexture());
            int tint = attributes.getTintColor();


            return new CauldronInventory.FluidAttributes(sprite, new Color(tint));
      }
}
