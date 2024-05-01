package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {
      @Inject(method = "handleRespawn", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/client/player/LocalPlayer;setId(I)V"))
      private void onRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci, ResourceKey resourceKey, Holder holder, LocalPlayer oldPlayer, int i, String string, LocalPlayer newPlayer) {
            BackData oldBackData = BackData.get(oldPlayer);
            BackData newBackData = BackData.get(newPlayer);
            NonNullList<ItemStack> oldStacks = oldBackData.getBackpackInventory().getItemStacks();
            BackpackInventory backpackInventory = newBackData.getBackpackInventory();
            backpackInventory.clearContent();
            backpackInventory.getItemStacks().addAll(oldStacks);
      }
}
