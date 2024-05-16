package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.items.recipes.SuperSpecialRecipe;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {
      @Inject(method = "slotChangedCraftingGrid", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/inventory/ResultContainer;setRecipeUsed(Lnet/minecraft/world/level/Level;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
      private static void stopSuperSpecialRecipe(AbstractContainerMenu $$0, Level $$1, Player player, CraftingContainer $$3, ResultContainer $$4, CallbackInfo ci, ServerPlayer $$5, ItemStack $$6, Optional $$7, CraftingRecipe craftingRecipe) {
            if (craftingRecipe instanceof SuperSpecialRecipe recipe) {
                  if (!recipe.isSuperSpecialPlayer(player)) {

                        $$4.setItem(0, $$6);
                        $$0.setRemoteSlot(0, $$6);
                        $$5.connection.send(new ClientboundContainerSetSlotPacket($$0.containerId, $$0.incrementStateId(), 0, $$6));
                        ci.cancel();
                  }
            }
      }
}
