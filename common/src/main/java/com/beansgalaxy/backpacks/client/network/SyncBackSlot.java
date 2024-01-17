package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.core.BackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SyncBackSlot {
      public static void receiveAtClient(UUID playerId, ItemStack stack) {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player == null)
                  return;

            Player otherPlayer = player.level().getPlayerByUUID(playerId);

            if (otherPlayer == null)
                  return;

            BackData backSlot = BackData.get(otherPlayer);
            backSlot.set(stack);
      }
}
