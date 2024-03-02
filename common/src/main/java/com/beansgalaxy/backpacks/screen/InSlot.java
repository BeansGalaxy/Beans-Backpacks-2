package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InSlot extends Slot {
      public static final ResourceLocation BACKPACK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
      public static final ResourceLocation INPUT = new ResourceLocation("sprites/empty_slot_input");
      public static final ResourceLocation INPUT_ALT = new ResourceLocation("sprites/empty_slot_input_alt");
      public final BackData backData;

      public InSlot(BackData backData) {
            super(backData.backpackInventory, 0, BackData.UV[0], BackData.UV[1]);
            this.backData = backData;
      }

      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(BACKPACK_ATLAS, Constants.SLOTS_MOD_ACTIVE ? INPUT_ALT : INPUT);
      }

      @Override
      public boolean isActive() {
            boolean creative = backData.owner.isCreative();
            boolean storage = Kind.isStorage(backData.getStack());
            boolean empty = backData.backpackInventory.isEmpty();
            return !creative && storage || !empty;
      }

      public boolean mayPlace(ItemStack stack) {
            return backData.backpackInventory.canPlaceItem(stack);
      }
}
