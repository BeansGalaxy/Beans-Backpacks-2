package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotInventory {
      public static final int MAX_SIZE = 128;

      public static ItemStack add(ItemStack pot, ItemStack inserted, Level level) {
            if (inserted.isEmpty() || inserted.hasTag() || !inserted.isStackable())
                  return null;

            int amount = inserted.getCount();
            int max_amount = inserted.getMaxStackSize() * MAX_SIZE;
            CompoundTag itemTag = pot.getOrCreateTagElement("back_slot");
            if (itemTag.contains("id") && itemTag.contains("amount")) {
                  String string = itemTag.getString("id");
                  Item stored = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  if (!inserted.is(stored)) return null;
                  int storedAmount = itemTag.getInt("amount");
                  if (storedAmount >= max_amount) return null;
                  amount += storedAmount;
            } else {
                  ResourceLocation key = BuiltInRegistries.ITEM.getKey(inserted.getItem());
                  itemTag.putString("id", key.toString());
            }

            if (amount < max_amount) {
                  itemTag.putInt("amount", amount);
                  inserted.setCount(0);
            } else {
                  itemTag.putInt("amount", max_amount);
                  inserted.setCount(amount - max_amount);
            }

            if (level.isClientSide())
                  Tooltip.playSound(Kind.POT, PlaySound.INSERT);

            return inserted;
      }

      public static ItemStack take(ItemStack pot, boolean doSplit, Level level) {
            if (!pot.hasTag())
                  return null;

            CompoundTag itemTag = pot.getTagElement("back_slot");
            if (itemTag == null)
                  return null;

            if (itemTag.contains("id") && itemTag.contains("amount")) {
                  String id = itemTag.getString("id");
                  Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
                  ItemStack stack = item.getDefaultInstance();
                  int maxStackSize = stack.getMaxStackSize();
                  int amount = itemTag.getInt("amount");
                  int count = Math.min(amount, maxStackSize);
                  if (doSplit) count = Math.max(1, count / 2);
                  amount -= count;
                  if (amount > 0) {
                        itemTag.putInt("amount", amount);
                        stack.setCount(count);
                  }
                  else pot.removeTagKey("back_slot");
                  stack.setCount(count);
                  if (level.isClientSide())
                        Tooltip.playSound(Kind.POT, PlaySound.TAKE);
                  return stack;
            }
            return null;
      }
}
