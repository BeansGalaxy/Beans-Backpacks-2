package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MenuSlot extends Slot {
      public static final int SPACING = 18;
      public static final int COLUMN_START = 4;
      public static final int MAX_ROWS = 3;
      public static final int ADD_ROW = 9;
      public static final int MAX_SLOTS = 69;
      public static final ResourceLocation INPUT = new ResourceLocation("sprites/empty_slot_input_large");
      public final int backIndex;
      public State state = State.HIDDEN;
      private final Runnable onClick;

      public MenuSlot(Container container, int index, Runnable onClick) {
            super(container, index, 0, 0);
            backIndex = index;
            this.onClick = onClick;
      }

      @Override
      public boolean isActive() {
            return !State.HIDDEN.equals(state);
      }

      @Override
      public boolean isHighlightable() {
            return !State.HIDDEN.equals(state);
      }

      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return null;
      }

      @Override
      public ItemStack getItem() {
            if (State.EMPTY.equals(state))
                  return ItemStack.EMPTY;
            return this.container.getItem(backIndex);
      }

      @Override
      public void set(ItemStack $$0) {
            this.container.setItem(backIndex, $$0);
            this.setChanged();
      }

      @Override
      public ItemStack remove(int $$0) {
            return this.container.removeItem(backIndex, $$0);
      }

      @Override
      public boolean mayPickup(Player $$0) {
            return !State.EMPTY.equals(state);
      }

      @Override
      public int getContainerSlot() {
            return this.backIndex;
      }

      @Override
      public void onTake(Player $$0, ItemStack $$1) {
            onClick.run();
            super.onTake($$0, $$1);
      }

      public enum State {
            ACTIVE,
            EMPTY,
            HIDDEN
      }

      public static int[] getXY(BackpackInventory inventory, int slot) {
            int size = Math.min(MAX_SLOTS, inventory.getContainerSize());
            int slots;
            int index;

            boolean hasSpace = inventory.spaceLeft() > 0;
            if (hasSpace) {
                  index = slot + 1;
                  slots = size + 1;
            }
            else {
                  index = slot;
                  slots = size + 1;
            }


            int columns = COLUMN_START;
            int limit = COLUMN_START * MAX_ROWS;
            if (slots > limit) {
                  columns = Mth.ceil(slots / (MAX_ROWS + 0.0));
                  if (columns % 2 == 1) columns += 1;
            }
            if (columns > 9) { // AT 9 COLUMNS ADD A 4th ROW
                  columns = Mth.ceil(slots / (MAX_ROWS + 1.0));
                  if (columns % 2 == 1) columns += 1;
            }
            int offsetY = 0;
            if (columns > 11) { // AT 11 COLUMNS ADD A 5th ROW
                  columns = Mth.ceil(slots / (MAX_ROWS + 2.0));
                  if (columns % 2 == 1) columns += 1;
                  offsetY = -9;
            }
            if (columns > 13) {
                  columns = 14;
            }

            int shift = slots % columns;
            int firstRowSize = (slots - 1) % columns + 1;
            float offsetX;
            if (index < shift)
                  offsetX = (columns * 2 - firstRowSize) / 2f;
            else
                  offsetX = Math.min(columns, slots) / 2f;

            int k = index + columns - shift;
            int column = k % columns;
            int x = (int) ((column - offsetX) * SPACING) + 89;

            int row = (index - firstRowSize + columns) / columns;
            int pos = row * SPACING;
            int rows = (slots - 1) / columns;
            int top = rows * (SPACING / 2);
            int y = (pos - top) + 99 + offsetY;

            return new int[]{x, y};
      }
}
