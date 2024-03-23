package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.data.BackData;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.SlotWrapper;
import net.minecraft.world.inventory.Slot;

public class WrapCreativeSlot extends SlotWrapper {
      public WrapCreativeSlot(Slot slot) {
            super(slot, 0, BackData.UV_CREATIVE[0], BackData.UV_CREATIVE[1]);
      }
}
