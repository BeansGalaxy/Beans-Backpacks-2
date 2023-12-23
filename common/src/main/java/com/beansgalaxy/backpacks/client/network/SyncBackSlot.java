package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SyncBackSlot {
      public static void receiveAtClient(UUID playerId, ItemStack stack) {
            Player otherPlayer = Minecraft.getInstance().player.level().getPlayerByUUID(playerId);

            if (otherPlayer == null)
                  return;

            BackSlot backSlot = BackSlot.get(otherPlayer);
            backSlot.set(stack);
      }
}
