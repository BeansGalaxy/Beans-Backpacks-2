package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.access.ClickAccessor;
import com.beansgalaxy.backpacks.access.PosAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingScreenMixin extends AbstractContainerScreen implements ClickAccessor, PosAccessor {
      public EffectRenderingScreenMixin(AbstractContainerMenu $$0, Inventory $$1, Component $$2) {
            super($$0, $$1, $$2);
      }

      @Override
      public void beans_Backpacks_2$instantPlace() {
            instantPlace(minecraft.player, hoveredSlot);
      }

      @Override
      public void beans_Backpacks_2$slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
            this.slotClicked($$0, $$1, $$2, $$3);
      }

      @Override
      public int[] getPos() {
            return new int[]{leftPos + (imageWidth / 2), topPos + (imageHeight / 2)};
      }
}
