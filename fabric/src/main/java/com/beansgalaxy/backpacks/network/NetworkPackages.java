package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.packages.*;
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
      public static final ResourceLocation CONFIG_TRAITS_2C = new ResourceLocation(Constants.MOD_ID, "backpack_traits_c");
      public static final ResourceLocation CONFIG_LISTS_2C = new ResourceLocation(Constants.MOD_ID, "backpack_lists_c");
      public static final ResourceLocation INSTANT_PLACE_2S = new ResourceLocation(Constants.MOD_ID, "instant_place_s");
      public static final ResourceLocation PICK_BACKPACK_2S = new ResourceLocation(Constants.MOD_ID, "pick_backpack_s");
      public static final ResourceLocation ENDER_POS_2C = new ResourceLocation(Constants.MOD_ID, "ender_pos_c");
      public static final ResourceLocation USE_CAULDRON_2S = new ResourceLocation(Constants.MOD_ID, "use_cauldron_s");
      public static final ResourceLocation CLEAR_BACK_SLOT_2S = new ResourceLocation(Constants.MOD_ID, "clear_back_slot_s");

      public static void registerC2SPackets() {
            ServerPlayNetworking.registerGlobalReceiver(SPRINT_KEY_2S, SprintKeyPacket2S::receiveAtServer);
            ServerPlayNetworking.registerGlobalReceiver(CALL_BACK_SLOT_2S, SyncBackSlot2All::callSyncBackSlot);
            ServerPlayNetworking.registerGlobalReceiver(CALL_BACK_INV_2S, SyncBackInventory2C::callSyncBackInventory);
            ServerPlayNetworking.registerGlobalReceiver(INSTANT_PLACE_2S, InstantPlace2S::receiveAtServer);
            ServerPlayNetworking.registerGlobalReceiver(PICK_BACKPACK_2S, PickBackpack::receiveAtServer);
            ServerPlayNetworking.registerGlobalReceiver(USE_CAULDRON_2S, UseCauldron2S::receiveAtServer);
            ServerPlayNetworking.registerGlobalReceiver(CLEAR_BACK_SLOT_2S, ClearBackSlot2S::receiveAtServer);
      }

      public static void registerS2CPackets() {
            ClientPlayNetworking.registerGlobalReceiver(SYNC_VIEWERS_2All, ReceiveAtClient::SyncViewers);
            ClientPlayNetworking.registerGlobalReceiver(SYNC_BACK_SLOT_2C, ReceiveAtClient::syncBackSlot);
            ClientPlayNetworking.registerGlobalReceiver(SYNC_BACK_INV_2C, ReceiveAtClient::SyncBackInventory);
            ClientPlayNetworking.registerGlobalReceiver(CONFIG_TRAITS_2C, ReceiveAtClient::ConfigTraits);
            ClientPlayNetworking.registerGlobalReceiver(CONFIG_LISTS_2C, ReceiveAtClient::ConfigLists);
            ClientPlayNetworking.registerGlobalReceiver(ENDER_POS_2C, ReceiveAtClient::recieveEnderPos);
      }
}
