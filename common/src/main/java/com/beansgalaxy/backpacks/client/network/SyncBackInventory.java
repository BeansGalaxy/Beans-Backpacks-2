package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class SyncBackInventory {

      public static void receiveAtClient(String stacks) {
            CompoundTag tag = stringToNbt(stacks);

            LocalPlayer player = Minecraft.getInstance().player;
            BackpackInventory backpackInventory = BackSlot.getInventory(player);
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
}
