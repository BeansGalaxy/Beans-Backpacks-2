package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class NetworkPackages {
      public static final ResourceLocation SPRINT_KEY_2S = new ResourceLocation(Constants.MOD_ID, "sprint_key_s");

      public static void registerC2SPackets() {
            ServerPlayNetworking.registerGlobalReceiver(SPRINT_KEY_2S, SprintKeyPacket::receiveAtServer);
      }

}
