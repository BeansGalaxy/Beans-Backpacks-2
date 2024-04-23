package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.access.ClickAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingScreenMixin extends AbstractContainerScreen implements ClickAccessor {
      public EffectRenderingScreenMixin(AbstractContainerMenu $$0, Inventory $$1, Component $$2) {
            super($$0, $$1, $$2);
      }

      @Override
      public void beans_Backpacks_2$instantPlace() {
            Slot hoveredSlot = this.hoveredSlot;
            if (hoveredSlot == null)
                  return;

            BackData backData = BackData.get(this.minecraft.player);
            if (hoveredSlot.getItem() == backData.getStack() && backData.backpackInventory.isEmpty())
                  return;

            this.slotClicked(hoveredSlot, hoveredSlot.index, 0, ClickType.PICKUP);
      }


      @Override
      public int[] getPos() {
            return new int[]{leftPos + (imageWidth / 2), topPos + (imageHeight / 2)};
      }
}
