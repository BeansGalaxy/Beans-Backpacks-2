package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class EnderBackpack extends BackpackItem {

      public EnderBackpack(Properties properties) {
            super(properties);
      }

      @Override
      public void verifyTagAfterLoad(CompoundTag tag) {
            super.verifyTagAfterLoad(tag);
      }

      public UUID getOrCreateUUID(UUID fallback, ItemStack stack) {
            CompoundTag tag = stack.getOrCreateTagElement("display");
            if (tag.contains("placed_by")) {
                  UUID uuid = tag.getUUID("placed_by");
                  if (ServerSave.MAPPED_ENDER_DATA.containsKey(uuid))
                        return uuid;
            }

            tag.putUUID("placed_by", fallback);
            return fallback;
      }

      public void setUUID(UUID uuid, ItemStack stack) {
            CompoundTag tag = stack.getOrCreateTagElement("display");
            tag.putUUID("placed_by", uuid);
      }

      @Override
      public void onCraftedBy(ItemStack stack, Level level, Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                  UUID uuid = player.getUUID();
                  setUUID(uuid, stack);
                  ServerSave.EnderData enderData = ServerSave.getEnderData(uuid);
                  enderData.setPlayerName(player.getName().copy());
                  CompoundTag tag = stack.getTag();
                  if (player.containerMenu instanceof ItemCombinerMenu && tag != null) {
                        CompoundTag trim = tag.getCompound("Trim");
                        enderData.setTrim(trim.copy());
                        tag.remove("Trim");
                  }

                  for (ServerPlayer players : serverPlayer.server.getPlayerList().getPlayers()) {
                        Services.NETWORK.sendEnderData2C(players, uuid);
                  }

                  super.onCraftedBy(stack, level, player);
            }
      }
}
