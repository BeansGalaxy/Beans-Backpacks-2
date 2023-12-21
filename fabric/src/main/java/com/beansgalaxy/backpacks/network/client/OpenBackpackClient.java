package com.beansgalaxy.backpacks.network.client;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.screen.BackpackScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class OpenBackpackClient {
      public static void receiveAtClient(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
            int containerId = buf.readInt();
            Inventory playerInventory = minecraft.player.getInventory();
            Backpack backpack = (Backpack) minecraft.level.getEntity(buf.readInt());
            BackpackInventory backpackInventory = backpack.getBackpackInventory();

            BackpackMenu backpackMenu = new BackpackMenu(containerId, playerInventory, backpackInventory);
            BackpackScreen backpackScreen = new BackpackScreen(backpackMenu, playerInventory, null);
            minecraft.player.containerMenu = backpackScreen.getMenu();
            minecraft.setScreen(backpackScreen);
      }
}
