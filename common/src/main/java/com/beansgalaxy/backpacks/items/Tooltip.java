package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class Tooltip {

      public static Optional<TooltipComponent> get(ItemStack stack) {
            Kind kind = Kind.fromStack(stack);
            if (kind == null)
                  return Optional.empty();

            Player player = Minecraft.getInstance().player;
            if (player == null)
                  return Optional.empty();

            // IS IN INVENTORY MENU
            if (!(player.containerMenu instanceof InventoryMenu))
                  return Optional.empty();

            // IS BACKPACK EQUIPPED
            BackData backData = BackData.get(player);
            ItemStack equippedOnBack = backData.getStack();
            if (!stack.equals(equippedOnBack) || backData.backpackInventory.isEmpty())
                  return Optional.empty();

            NonNullList<ItemStack> defaultedList = NonNullList.create();
            NonNullList<ItemStack> backpackList = BackData.get(player).backpackInventory.getItemStacks();
            backpackList.forEach(itemstack -> defaultedList.add(itemstack.copy()));
            if (!defaultedList.isEmpty())
            {
                  for (int j = 0; j < defaultedList.size(); j++)
                  {
                        ItemStack itemStack = defaultedList.get(j);
                        int count = defaultedList.stream()
                                    .filter(itemStacks -> itemStacks.is(itemStack.getItem()) && !itemStack.equals(itemStacks))
                                    .mapToInt(itemStacks -> itemStacks.copyAndClear().getCount()).sum();
                        itemStack.setCount(count + itemStack.getCount());
                        defaultedList.removeIf(ItemStack::isEmpty);
                  }
            }
            int totalWeight = getBundleOccupancy(defaultedList) / backData.backpackInventory.getLocalData().maxStacks();
            return Optional.of(new BundleTooltip(defaultedList, totalWeight));
      }

      private static int getItemOccupancy(ItemStack stack) {
            CompoundTag nbtCompound;
            if ((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag() &&
                        (nbtCompound = BlockItem.getBlockEntityData(stack)) != null &&
                        !nbtCompound.getList("Bees", Tag.TAG_COMPOUND).isEmpty()) {
                  return 64;
            }
            return 64 / stack.getMaxStackSize();
      }

      private static int getBundleOccupancy(NonNullList<ItemStack> defaultedList) {
            return defaultedList.stream().mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
      }

      /** LORE AND NAME **/
      private static String key;

      public static Component name(ItemStack stack) {
            String key = stack.getOrCreateTagElement("display").getString("key");
            return Component.literal(Traits.get(key).name);
      }

      public static void lore(ItemStack stack, List<Component> components) {
            Minecraft instance = Minecraft.getInstance();
            LocalPlayer player = instance.player;
            if (player == null)
                  return;

            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            if (stack == backStack && backData.backpackInventory.isEmpty()) {
                  key = "§6" + getKeyBinding().getTranslatedKeyMessage().getString()
                              .replace("Left ", "L")
                              .replace("Right ", "R")
                              .replace("Control", "Ctrl");

                  if (backData.actionKeyPressed) {
                        String useKey = "§6" + instance.options.keyUse.getTranslatedKeyMessage().getString()
                                    .replace("Right Button", "RClick")
                                    .replace("Left Button", "LClick")
                                    .replace("Left ", "L")
                                    .replace("Right ", "R");

                        MutableComponent empty = Component.literal("");
                        if (Services.COMPAT.isModLoaded(CompatHelper.CURIOS))
                              components.add(empty);

                        components.add(Component.translatable("tooltip.beansbackpacks.empty_0", key));
                        components.add(Component.translatable("tooltip.beansbackpacks.empty_1", key));
                        components.add(empty);
                        components.add(Component.translatable("tooltip.beansbackpacks.empty_2", key));
                        MutableComponent e3 = Component.translatable("tooltip.beansbackpacks.empty_3", key);
                        if (!e3.getString().isEmpty())
                              components.add(e3);
                        components.add(empty);
                        components.add(Component.translatable("tooltip.beansbackpacks.empty_4", key, useKey));
                        components.add(Component.translatable("tooltip.beansbackpacks.empty_5", key, useKey));
                        MutableComponent e5 = Component.translatable("tooltip.beansbackpacks.empty_6", key, useKey);
                        if (!e5.getString().isEmpty())
                              components.add(e5);
                        if (Services.COMPAT.isModLoaded(CompatHelper.TRINKETS))
                              components.add(empty);

                  } else {
                        components.add(Component.translatable("tooltip.beansbackpacks.empty_title_1", key));
                        MutableComponent t2 = Component.translatable("tooltip.beansbackpacks.empty_title_2", key);
                        if (!t2.getString().isEmpty()) components.add(t2);
                  }
            }


      }

      public static KeyMapping getKeyBinding() {
            Minecraft instance = Minecraft.getInstance();
            KeyMapping sprintKey = instance.options.keySprint;
            KeyMapping customKey = KeyPress.INSTANCE.ACTION_KEY;

            boolean isCustomUnbound = customKey.same(sprintKey) || customKey.isUnbound();

            return isCustomUnbound ? sprintKey : customKey;
      }

      public static boolean isBarVisible(ItemStack stack) {
            LocalPlayer player = Minecraft.getInstance().player;
            BackData backSlot = BackData.get(player);

            return backSlot.getStack() == stack && !backSlot.backpackInventory.isEmpty();
      }

      public static int getBarWidth(ItemStack stack) {
            LocalPlayer player = Minecraft.getInstance().player;
            BackData backData = BackData.get(player);

            if (backData.getStack() != stack)
                  return 13;

            BackpackInventory backpackInventory = backData.backpackInventory;
            int spaceLeft = backpackInventory.spaceLeft();
            int maxStacks = backpackInventory.getLocalData().maxStacks();

            if (spaceLeft < 1) {
                  barColor = FULL_COLOR;
                  return 13;
            } else
                  barColor = BAR_COLOR;

            int barWidth = spaceLeft * 13 / (maxStacks * 64);

            return 13 - barWidth;
      }

      private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
      private static final int FULL_COLOR = Mth.color(0.9F, 0.2F, 0.3F);
      public static int barColor = BAR_COLOR;
}
