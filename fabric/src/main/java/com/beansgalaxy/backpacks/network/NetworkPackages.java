package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket;
import com.beansgalaxy.backpacks.network.packages.SyncBackInventory2C;
import com.beansgalaxy.backpacks.network.packages.SyncBackSlotS2All;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkPackages {
      public static final ResourceLocation SPRINT_KEY_2S = new ResourceLocation(Constants.MOD_ID, "sprint_key_s");
      public static final ResourceLocation SYNC_VIEWERS_2All = new ResourceLocation(Constants.MOD_ID, "sync_viewers_a");
      public static final ResourceLocation SYNC_BACK_SLOT_2C = new ResourceLocation(Constants.MOD_ID, "back_slot_a");
      public static final ResourceLocation CALL_BACK_SLOT_2S = new ResourceLocation(Constants.MOD_ID, "call_back_slot_s");
      public static final ResourceLocation SYNC_BACK_INV_2C = new ResourceLocation(Constants.MOD_ID, "backpack_inventory_c");
      public static final ResourceLocation CALL_BACK_INV_2S = new ResourceLocation(Constants.MOD_ID, "call_backpack_inventory_s");

      public static void registerC2SPackets() {
            ServerPlayNetworking.registerGlobalReceiver(SPRINT_KEY_2S, SprintKeyPacket::receiveAtServer);
            ServerPlayNetworking.registerGlobalReceiver(CALL_BACK_SLOT_2S, SyncBackSlotS2All::callSyncBackSlot);
            ServerPlayNetworking.registerGlobalReceiver(CALL_BACK_INV_2S, SyncBackInventory2C::callSyncBackInventory);
      }

      public static void registerS2CPackets() {
            ClientPlayNetworking.registerGlobalReceiver(SYNC_VIEWERS_2All, ReceiveAtClient::SyncViewers);
            ClientPlayNetworking.registerGlobalReceiver(SYNC_BACK_SLOT_2C, ReceiveAtClient::SyncBackSlot);
            ClientPlayNetworking.registerGlobalReceiver(SYNC_BACK_INV_2C, ReceiveAtClient::SyncBackInventory);
      }

}
