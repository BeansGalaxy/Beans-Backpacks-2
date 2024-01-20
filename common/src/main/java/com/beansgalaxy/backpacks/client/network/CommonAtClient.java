package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class CommonAtClient {
      public static void syncBackSlot(UUID playerId, ItemStack stack) {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player == null)
                  return;

            Player otherPlayer = player.level().getPlayerByUUID(playerId);

            if (otherPlayer == null)
                  return;

            BackData backSlot = BackData.get(otherPlayer);
            backSlot.set(stack);
      }

      public static void syncBackInventory(String stacks) {
            CompoundTag tag = stringToNbt(stacks);

            LocalPlayer player = Minecraft.getInstance().player;
            BackpackInventory backpackInventory = BackData.get(player).backpackInventory;
            backpackInventory.getItemStacks().clear();

            backpackInventory.readStackNbt(tag);
      }

      public static CompoundTag stringToNbt(String string) {
            try {
                  CompoundTag nbt = NbtUtils.snbtToStructure(string);
                  return nbt;
            } catch (CommandSyntaxException e) {
                  throw new RuntimeException(Constants.MOD_ID + ": Failed to sync BackpackInventory with networking");
            }
      }

      public static void syncViewersPacket(int id, byte viewers) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(id);
            if (entity instanceof BackpackEntity backpackEntity)
                  backpackEntity.viewable.viewers = viewers;
            else if (entity instanceof Player player) {
                  BackData.get(player).backpackInventory.getViewable().viewers = viewers;
            }
      }
}
