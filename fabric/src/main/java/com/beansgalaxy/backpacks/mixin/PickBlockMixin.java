package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.events.PickBlockEvent;
import com.beansgalaxy.backpacks.events.UseKeyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Minecraft.class)
public abstract class PickBlockMixin {

      @Shadow @Nullable public LocalPlayer player;

      @Shadow @Nullable public HitResult hitResult;

      @Inject(method = "pickBlock", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
      private void pickFromBackpack(CallbackInfo ci, boolean instantBuild, BlockEntity $$1, ItemStack itemStack, HitResult.Type $$2, Inventory inventory) {
            if (PickBlockEvent.cancelPickBlock(instantBuild, inventory, itemStack, player))
                  ci.cancel();
      }

      @Inject(method = "startUseItem", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true,
                  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
      private void interruptUseItem(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand interactionHand) {
            if (interactionHand.equals(InteractionHand.MAIN_HAND)) {
                  if (UseKeyEvent.tryUseCauldron(this.player, this.hitResult))
                        ci.cancel();

            }
      }
}
