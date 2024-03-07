package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaceBackpackEvent;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayMixin {

      @Shadow public ServerPlayer player;

      @Redirect(method = "handleUseItem", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItem(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
      public InteractionResult interactItem(ServerPlayerGameMode instance, ServerPlayer player, Level world, ItemStack stack, InteractionHand hand) {
            InteractionResult actionResult = this.player.gameMode.useItem(this.player, world, stack, hand);
            PlaceBackpackEvent.cancelCoyoteClick(player, actionResult, true);
            return actionResult;
      }

      @Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
      public InteractionResult interactBlock(ServerPlayerGameMode instance, ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult) {
            InteractionResult actionResult = this.player.gameMode.useItemOn(this.player, world, stack, hand, hitResult);
            PlaceBackpackEvent.cancelCoyoteClick(player, actionResult, false);
            return actionResult;
      }


      public void pickFromBackpack(ServerboundPickItemPacket packet, CallbackInfo ci) {

            int slot = packet.getSlot();
            if (slot < 0) {
                  int backpackSlot = (slot + 100) * -1;
                  if (backpackSlot < 0) {
                        ci.cancel();
                        return;
                  }

                  PacketUtils.ensureRunningOnSameThread(packet,(ServerGamePacketListener) this, this.player.serverLevel());
                  BackData backData = BackData.get(player);
                  Inventory inventory = player.getInventory();

                  Kind kind = Kind.fromStack(backData.getStack());
                  if (inventory.getFreeSlot() == -1)
                  {
                        PlaySound.HIT.at(player, kind, 0.1f);
                        ci.cancel();
                        return;
                  }

                  int overflowSlot = -1;
                  ItemStack selectedStack = inventory.getItem(inventory.selected);
                  inventory.setItem(inventory.selected, backData.backpackInventory.removeItemSilent(backpackSlot));
                  PlaySound.TAKE.at(player, kind);

                  if (!selectedStack.isEmpty())
                  {
                        overflowSlot = inventory.getFreeSlot();
                        inventory.setItem(overflowSlot, selectedStack);
                  }

                  this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, inventory.selected, selectedStack));
                  this.player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));
                  Services.NETWORK.backpackInventory2C(player);
                  if (overflowSlot > -1)
                        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, overflowSlot, inventory.getItem(overflowSlot)));
                  ci.cancel();
            }
      }
}
