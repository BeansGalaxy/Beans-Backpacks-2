package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaceBackpackEvent;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

      @Inject(method = "handleSetCreativeModeSlot", cancellable = true,
                  at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;getSlotNum()I"))
      public void setCreativeBackSlot(ServerboundSetCreativeModeSlotPacket ctx, CallbackInfo ci) {
            int slotIndex = ctx.getSlotNum();
            ItemStack stack = ctx.getItem();

            boolean outOfRange = player.inventoryMenu.slots.size() < slotIndex;
            boolean flag2 = stack.isEmpty() || stack.getDamageValue() >= 0 && stack.getCount() <= 64 && !stack.isEmpty();
            if (!outOfRange && flag2) {
                  Slot slot = this.player.inventoryMenu.getSlot(slotIndex);
                  if (Services.COMPAT.isBackSlot(slot)) {
                        slot.setByPlayer(stack);
                        this.player.inventoryMenu.broadcastChanges();
                        ci.cancel();
                  }
            }
      }
}
