package com.beansgalaxy.backpacks.access;

import com.beansgalaxy.backpacks.data.BackData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public interface ClickAccessor {
      void beans_Backpacks_2$instantPlace();

      default void instantPlace(Player player, Slot hoveredSlot) {
            if (hoveredSlot == null)
                  return;

            BackData backData = BackData.get(player);
            if (hoveredSlot.getItem() == backData.getStack() && backData.getBackpackInventory().isEmpty())
                  return;

            this.beans_Backpacks_2$slotClicked(hoveredSlot, hoveredSlot.index, 0, ClickType.PICKUP);
      }

      void beans_Backpacks_2$slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3);
}
