package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.awt.*;
import java.util.List;

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
      public List<ItemStack> backSlotDisabled(Player owner) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.backSlotDisabled(owner);
            return List.of();
      }

      @Override
      public boolean isBackSlot(Slot slot) {
            if (isModLoaded(TRINKETS))
                  return TrinketsRegistry.isBackSlot(slot);
            return slot instanceof BackSlot;
      }

      @Override
      public CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas) {
            FluidVariant fluidVariant = FluidVariant.of(fluid);
            TextureAtlasSprite sprite = FluidVariantRendering.getSprite(fluidVariant);
            int color = FluidVariantRendering.getColor(fluidVariant);
            return new CauldronInventory.FluidAttributes(sprite, new Color(color));
      }

      @Override
      public boolean invokeListenersOnDeath(BackData backData) {
            Context context = new Context(backData);
            OnDeathCallback.EVENT.invoker().onDeath(context);
            return context.isCancelled;
      }

      public interface OnDeathCallback {
            Event<OnDeathCallback> EVENT = EventFactory.createArrayBacked(OnDeathCallback.class, listeners -> (context) -> {
                  for (OnDeathCallback listener : listeners) {
                        listener.onDeath(context);
                  }
            });

            void onDeath(Context context);
      }

      public class Context {
            private final BackData backData;
            private boolean isCancelled = false;

            public Context(BackData backData) {
                  this.backData = backData;
            }

            public Player getPlayer() {
                  return backData.owner;
            }

            public Container getBackInventory() {
                  return backData.getBackpackInventory();
            }

            public ItemStack getBackStack() {
                  return backData.getStack();
            }

            public void setBackStack(ItemStack backStack) {
                  backData.set(backStack);
            }

            public boolean keepBackSlotGamerule() {
                  return ServerSave.GAMERULES.get(Gamerules.KEEP_BACK_SLOT);
            }

            public void cancel() {
                  isCancelled = true;
            }
      }
}
