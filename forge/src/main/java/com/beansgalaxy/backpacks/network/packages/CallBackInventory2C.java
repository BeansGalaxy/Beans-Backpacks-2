package com.beansgalaxy.backpacks.network.packages;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.UUID;

public class CallBackInventory2C {
      public static void register() {
            NetworkPackages.INSTANCE.messageBuilder(CallBackInventory2C.class, NetworkDirection.PLAY_TO_SERVER)
                        .encoder(CallBackInventory2C::encode).decoder(CallBackInventory2C::new).consumerMainThread(CallBackInventory2C::handle).add();
      }

      final UUID uuid;

      public CallBackInventory2C(FriendlyByteBuf buf) {
            this(buf.readUUID());
      }

      public CallBackInventory2C(UUID uuid) {
            this.uuid = uuid;
      }

      public static void call(Player player) {
            UUID uuid = player.getUUID();
            NetworkPackages.C2S(new CallBackInventory2C(uuid));
      }

      public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
      }

      public void handle(CustomPayloadEvent.Context context) {
            Services.NETWORK.backpackInventory2C(context.getSender());
      }
}
