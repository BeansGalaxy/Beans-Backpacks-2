package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
            if (stack.equals(equippedOnBack)) {
                  if (equippedOnBack.getTagElement("back_slot") != null)
                        return specialTooltip(equippedOnBack, backData);
                  if (!backData.backpackInventory.isEmpty())
                        return backpackTooltip(player, backData);
            }
            return Optional.empty();
      }

      private static Optional<TooltipComponent> specialTooltip(ItemStack equippedOnBack, BackData backData) {
            return Optional.empty();
      }

      @NotNull
      private static Optional<TooltipComponent> backpackTooltip(Player player, BackData backData) {
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
            int totalWeight = getBundleOccupancy(defaultedList) / backData.backpackInventory.getTraits().maxStacks();
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
      public static Component name(ItemStack stack) {
            return Component.literal(Kind.getTraits(stack).name);
      }

      private static final MutableComponent empty = Component.literal("");
      private static String keyBind = "§6" + getKeyBinding().getTranslatedKeyMessage().getString()
                  .replace("Left ", "L")
                  .replace("Right ", "R")
                  .replace("Control", "Ctrl");

      public static void loreTitle(List<Component> components) {
            components.add(Component.translatable("tooltip.beansbackpacks.empty_title_1", keyBind));
            MutableComponent t2 = Component.translatable("tooltip.beansbackpacks.empty_title_2", keyBind);
            if (!t2.getString().isEmpty()) components.add(t2);
      }

      public static void nullTitle(List<Component> components) {
            components.add(Component.translatable("tooltip.beansbackpacks.null_title", Component.literal(keyBind.toLowerCase()).withStyle(ChatFormatting.GRAY)));
      }

      public static List<Component> addLore(List<Component> components, String kind, int lines) {
            Minecraft instance = Minecraft.getInstance();
            String useKey = "§6" + instance.options.keyUse.getTranslatedKeyMessage().getString()
                    .replace("Right Button", "RClick")
                    .replace("Left Button", "LClick")
                    .replace("Left ", "L")
                    .replace("Right ", "R");

            for (int i = 0; i <= lines; i++) {
                  components.add(Component.translatable("tooltip.beansbackpacks.help." + kind + i, keyBind, useKey));

                  if (i != lines && (i + 1) % 2 == 0)
                        components.add(empty);
            }

            return components;
      }

      public static List<Component> addLoreEnder(List<Component> components, MutableComponent playerName) {
            int lines = 5;
            for (int i = 0; i <= lines; i++) {
                  components.add(Component.translatable("tooltip.beansbackpacks.help.ender" + i, playerName));

                  if (i != lines && (i + 1) % 2 == 0)
                        components.add(empty);
            }

            return components;
      }

      public static List<Component> addNullLore(List<Component> components, Player player) {
            components.add(Component.translatable("tooltip.beansbackpacks.help.null0"));
            components.add(Component.translatable("tooltip.beansbackpacks.help.null1"));
            components.add(empty);
            components.add(Component.translatable("tooltip.beansbackpacks.help.null2"));
            components.add(Component.translatable("tooltip.beansbackpacks.help.null3"));
            components.add(Component.translatable("tooltip.beansbackpacks.help.null4"));
            components.add(empty);
            components.add(Component.translatable("tooltip.beansbackpacks.help.null5"));
            components.add(Component.translatable("tooltip.beansbackpacks.help.null6"));
            components.add(empty);
            components.add(Component.translatable("tooltip.beansbackpacks.help.null7"));
            components.add(Component.translatable("tooltip.beansbackpacks.help.null8", player.getName().plainCopy().withStyle(ChatFormatting.GOLD)));

            return components;
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
            int maxStacks = backpackInventory.getTraits().maxStacks();

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

      public static void playSound(Kind kind, PlaySound playSound) {
            playSound(kind, playSound, 0.3f);
      }

      public static void playSound(Kind kind, PlaySound playSound, float volume) {
            Random rnd = new Random();
            float pitch = playSound.isRandom() ? (rnd.nextFloat() / 4f) + 0.8f : 1f;
            playSound(Services.REGISTRY.getSound(kind, playSound), pitch, volume);
      }

      public static void playSound(SoundEvent soundEvent, float pitch, float volume) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
      }
}
