package com.beansgalaxy.backpacks.inventory;

import com.beansgalaxy.backpacks.config.IConfig;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotInventory {
      public static int getMaxSize() {
            return Traits.get("POT").getMaxStacks();
      }

      public static Item getContent(CompoundTag itemTag) {
            if (itemTag.contains("id")) {
                  String string = itemTag.getString("id");
                  return BuiltInRegistries.ITEM.get(new ResourceLocation(string));
            }
            return null;
      }

      public static ItemStack add(ItemStack pot, ItemStack inserted, Player player) {
            Level level = player.level();
            if (inserted.isEmpty() || (inserted.isStackable() && inserted.hasTag()))
                  return null;

            if (IConfig.blacklistedItem(inserted.getItem()))
                  return null;

            int amount = inserted.getCount();
            int max_amount = inserted.getMaxStackSize() * getMaxSize();

            if (max_amount == 0)
                  return null;
            
            CompoundTag itemTag = pot.getOrCreateTagElement("back_slot");
            Item content = getContent(itemTag);
            if (content != null && itemTag.contains("amount")) {
                  int storedAmount = itemTag.getInt("amount");
                  if (storedAmount >= max_amount) return null;
                  amount += storedAmount;
                  if (!inserted.is(content)) return null;
            } else {
                  ResourceLocation key = BuiltInRegistries.ITEM.getKey(inserted.getItem());
                  itemTag.putString("id", key.toString());
            }

            if (inserted.hasTag() && !inserted.isStackable()) {
                  CompoundTag tag = inserted.getTag();
                  itemTag.put(String.valueOf(amount), tag);
            }

            if (amount < max_amount) {
                  itemTag.putInt("amount", amount);
                  inserted.setCount(0);
            } else {
                  itemTag.putInt("amount", max_amount);
                  inserted.setCount(amount - max_amount);
                  if (player instanceof ServerPlayer serverPlayer)
                        Services.REGISTRY.triggerSpecial(serverPlayer, SpecialCriterion.Special.FILLED_POT);
            }

            if (level.isClientSide())
                  Tooltip.playSound(Traits.Sound.CLAY, PlaySound.INSERT);

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
                  if (itemTag.contains(String.valueOf(amount))) {
                        CompoundTag tag = itemTag.getCompound(String.valueOf(amount));
                        stack.setTag(tag);
                  }
                  amount -= count;
                  if (amount > 0) {
                        itemTag.putInt("amount", amount);
                        stack.setCount(count);
                  }
                  else pot.removeTagKey("back_slot");
                  stack.setCount(count);
                  if (level.isClientSide())
                        Tooltip.playSound(Traits.Sound.CLAY, PlaySound.TAKE);
                  return stack;
            }
            return null;
      }
}
