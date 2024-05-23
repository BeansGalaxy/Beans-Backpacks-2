package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

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
      public List<ItemStack> backSlotDisabled(Player owner) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.backSlotDisables(owner);
            return List.of();
      }

      @Override
      public boolean isBackSlot(Slot slot) {
            if (isModLoaded(CURIOS))
                  return CurioRegistry.isBackSlot(slot);
            return slot instanceof BackSlot;
      }

      @Override
      public CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas) {
            IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluid);
            TextureAtlasSprite sprite = blocksAtlas.getSprite(attributes.getStillTexture());
            int tint = attributes.getTintColor();


            return new CauldronInventory.FluidAttributes(sprite, new Color(tint));
      }

      @Override
      public boolean invokeListenersOnDeath(BackData backData) {
            OnDeath backSlotOnDeath = new OnDeath(backData);
            MinecraftForge.EVENT_BUS.post(backSlotOnDeath);
            return backSlotOnDeath.isCanceled();
      }

      @Cancelable
      public class OnDeath extends PlayerEvent {
            private final BackData backData;

            public OnDeath(BackData backData) {
                  super(backData.owner);
                  this.backData = backData;
            }

            public Container getBackInventory() {
                  return backData.getBackpackInventory();
            }

            public ItemStack getBackStack() {
                  return backData.getStack();
            }

            public void setBackStack(@NotNull ItemStack backStack) {
                  backData.set(backStack);
            }

            public boolean keepBackSlotGamerule() {
                  return ServerSave.GAMERULES.get(Gamerules.KEEP_BACK_SLOT);
            }
      }
}
