package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.events.PlaceBackpackEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
