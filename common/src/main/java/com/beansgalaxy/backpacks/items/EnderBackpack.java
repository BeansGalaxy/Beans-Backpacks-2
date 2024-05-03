package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderDisplay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EnderBackpack extends BackpackItem {

      public EnderBackpack(Properties properties) {
            super(properties);
      }

      @Override @Deprecated // Since 20.1-0.18-v2
      public void verifyTagAfterLoad(CompoundTag tag) {
            if (tag.contains("display")) {
                  CompoundTag display = tag.getCompound("display");
                  if (display.contains("placed_by")) {
                        UUID uuid = display.getUUID("placed_by");
                        tag.putUUID("owner", uuid);
                        display.remove("placed_by");
                        if (display.isEmpty())
                              tag.remove("display");
                  }
            }
            super.verifyTagAfterLoad(tag);
      }

      public UUID getOrCreateUUID(Player viewer, ItemStack stack) {
            UUID uuid = viewer.getUUID();
            Level level = viewer.level();
            return getOrCreateUUID(uuid, level, stack);
      }

      public UUID getOrCreateUUID(UUID uuid, Level level, ItemStack stack) {
            CompoundTag tag = stack.getTag();
            if (stack.hasTag() && tag.contains("owner")) {
                  uuid = tag.getUUID("owner");
            }

            EnderStorage.getEnderData(uuid, level);
            stack.getOrCreateTag().putUUID("owner", uuid);
            return uuid;
      }

      public void setUUID(UUID uuid, ItemStack stack) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putUUID("owner", uuid);
      }

      public boolean isPersistent(ItemStack stack) {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                  return false;
            }
            return tag.contains("owner") && tag.getBoolean("persistent_ender");
      }

      @Override
      public void onCraftedBy(ItemStack stack, Level level, Player player) {
            if (!isPersistent(stack) && player instanceof ServerPlayer serverPlayer) {
                  UUID uuid = player.getUUID();
                  setUUID(uuid, stack);
                  EnderInventory enderData = EnderStorage.getEnderData(uuid, level);
                  enderData.setPlayerName(player.getName().copy());
                  CompoundTag tag = stack.getTag();
                  if (player.containerMenu instanceof ItemCombinerMenu && tag != null) {
                        CompoundTag trim = tag.getCompound("Trim");
                        enderData.setTrim(trim.copy());
                        tag.remove("Trim");
                  }

                  for (ServerPlayer players : serverPlayer.server.getPlayerList().getPlayers()) {
                        SendEnderDisplay.send(players, uuid);
                  }
            }
            super.onCraftedBy(stack, level, player);
      }

      public boolean lockEnder(Player player, ItemStack backpackStack) {
            UUID playerUUID = player.getUUID();
            UUID ownerUUID = this.getOrCreateUUID(player, backpackStack);
            if (playerUUID.equals(ownerUUID)) {
                  EnderInventory enderData = EnderStorage.getEnderData(player);
                  enderData.setLocked(!enderData.isLocked());
                  SendEnderDisplay.send(player);
                  EnderStorage.get(player.level()).forEachViewing(ownerUUID, viewer -> {}, equipped -> {
                        if (equipped.owner.getUUID() != ownerUUID) {
                              equipped.drop();
                        }
                  });
                  enderData.clearViewers();
                  return true;
            }
            return false;
      }
}
