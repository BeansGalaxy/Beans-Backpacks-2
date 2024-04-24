package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.inventory.*;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
                        return backpackTooltip(backData);
            }
            return Optional.empty();
      }

      private static Optional<TooltipComponent> specialTooltip(ItemStack backStack, BackData backData) {
            CompoundTag backTag = backStack.getTagElement("back_slot");
            Traits.LocalData traits = backData.getTraits();
            if (backTag != null && backTag.contains("id") && backTag.contains("amount")) {
                  String string = backTag.getString("id");
                  Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                  int amount = backTag.getInt("amount");
                  switch (traits.kind) {
                        case POT -> {
                              return Optional.of(new SpecialTooltip.Pot(item, amount));
                        }
                        case CAULDRON -> {
                              return Optional.of(new SpecialTooltip.Cauldron(item, amount));
                        }
                  }
            }
            return Optional.empty();
      }

      @NotNull
      private static Optional<TooltipComponent> backpackTooltip(BackData backData) {
            NonNullList<ItemStack> backpackList = backData.backpackInventory.getItemStacks();
            if (backpackList.isEmpty())
                  return Optional.empty();

            return Optional.of(new BackpackTooltip(backpackList));
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
      public static String keyBind = "§6" + getKeyBinding().getTranslatedKeyMessage().getString()
                  .replace("Left ", "L")
                  .replace("Right ", "R")
                  .replace("Control", "Ctrl");

      public static void nullTitle(List<Component> components) {
            components.add(Component.translatable("tooltip.beansbackpacks.null_title", Component.literal(keyBind).withStyle(ChatFormatting.GOLD)));
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
                  return 14;
            } else if (spaceLeft < 65) {
                  float progress = 1 - spaceLeft / 64f;
                  float ease = progress * 1.6f;
                  float i = Mth.clamp(ease + 0.5f, 0, 1);
                  float j = Mth.clamp(ease - 0.6f, 0, 1);
                  barColor = Mth.color(Mth.lerp(i, 0f, 0.9f), Mth.lerp(j, 0.9f, 0.4f), 0.3f);
            } else
                  barColor = BAR_COLOR;

            int barWidth = Mth.ceil(spaceLeft * 13 / (maxStacks * 64.0));

            return 14 - barWidth;
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

      public static void appendTooltip(Player player, TooltipFlag flag, List<Component> components, Traits.LocalData traits, ItemStack instance) {
            Kind kind = traits.kind;
            switch (kind) {
                  case POT -> {
                        if (PotInventory.getMaxSize() == 0) return;
                        components.add(empty);
                        if (!instance.hasTag() || !instance.getTag().contains("back_slot")) {
                              components.add(Component.translatable("tooltip.beansbackpacks.special_title"));
                              components.add(Component.translatable("tooltip.beansbackpacks.storage.stacks",
                                          "§9" + PotInventory.getMaxSize()));
                              components.add(Component.translatable("tooltip.beansbackpacks.pot"));
                        }
                  }
                  case CAULDRON -> {
                        if (CauldronInventory.getMaxSize() == 0) return;
                        components.add(empty);
                        if (!instance.hasTag() || !instance.getTag().contains("back_slot")) {
                              components.add(Component.translatable("tooltip.beansbackpacks.special_title"));
                              components.add(Component.translatable("tooltip.beansbackpacks.cauldron",
                                          "§9" + (CauldronInventory.getMaxSize() / 4)));
                        }
                  }
                  default -> {
                        components.add(empty);
                        components.add(Component.translatable("tooltip.beansbackpacks.storage_title"));

                        int maxStacks = traits.maxStacks();
                        components.add(Component.translatable("tooltip.beansbackpacks.storage.stacks", "§9" + maxStacks));

                        if (traits.fireResistant())
                              components.add(Component.translatable("tooltip.beansbackpacks.storage.fire_resistant"));

                        if (Kind.ENDER.is(kind)) {
                              EnderBackpack enderBackpack = (EnderBackpack) instance.getItem();
                              UUID uuid = enderBackpack.getOrCreateUUID(player.getUUID(), instance);
                              Level level = player.level();
                              EnderStorage.Data enderData = EnderStorage.getEnderData(uuid, level);
                              MutableComponent playerName = enderData.getPlayerNameColored(level.registryAccess());
                              components.add(Component.translatable("tooltip.beansbackpacks.ender", playerName));
                        }

                        if (instance.hasTag() && instance.getTag().getBoolean("Locked"))
                              components.add(Component.literal("Locked").withStyle(ChatFormatting.BLUE));

                        String key = traits.backpack_id;
                        if (flag.isAdvanced() && !Constants.isEmpty(key))
                              components.add(Component.translatable("tooltip.beansbackpacks.hidden.backpack_id", "§8" + key));
                  }
            }
      }

      private static final ResourceLocation SLOT_BACKPACK = new ResourceLocation("sprites/empty_slot_backpack");
      private static final ResourceLocation SLOT_ELYTRA = new ResourceLocation("sprites/empty_slot_elytra");
      private static final ResourceLocation SLOT_CAULDRON = new ResourceLocation("sprites/empty_slot_cauldron");
      private static final ResourceLocation SLOT_POT = new ResourceLocation("sprites/empty_slot_pot");

      public static List<ResourceLocation> getTextures() {
            NonNullList<ResourceLocation> list = NonNullList.create();
            list.add(SLOT_BACKPACK);

            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null)
                  return list;

            AdvancementList advancements = connection.getAdvancements().getAdvancements();
            if (Kind.isWearableElytra(Items.ELYTRA.asItem()) && advancements.get(ResourceLocation.tryParse("end/root")) != null)
                  list.add(SLOT_ELYTRA);

            if (advancements.get(ResourceLocation.tryParse("beansbackpacks:info/decorated_pots")) != null)
                  list.add(SLOT_POT);

            if (advancements.get(ResourceLocation.tryParse("beansbackpacks:info/fluid_cauldrons")) != null)
                  list.add(SLOT_CAULDRON);


            return list;
      }

      public static boolean isCuriosMenu() {
            Minecraft minecraft = Minecraft.getInstance();
            boolean b = minecraft.player.containerMenu instanceof InventoryMenu;
            boolean b1 = minecraft.screen instanceof InventoryScreen;
            boolean b2 = Services.COMPAT.isModLoaded(CompatHelper.CURIOS);
            return b2 && b && !b1;
      }
}
