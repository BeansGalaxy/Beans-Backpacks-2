package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.ServerSave;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

      @Inject(method = "onTake", at = @At("TAIL"))
      private void updateEnderDataTrim(Player player, ItemStack stack, CallbackInfo ci) {
            if (Kind.ENDER.is(stack) && player instanceof ServerPlayer serverPlayer) {
                  ServerSave.EnderData enderData = ServerSave.getEnderData(player.getUUID());
                  CompoundTag tag = stack.getTag();

                  if (tag == null)
                        return;

                  CompoundTag trim = tag.getCompound("Trim");
                  enderData.setTrim(trim);
                  for (ServerPlayer players : serverPlayer.server.getPlayerList().getPlayers()) {
                        Services.NETWORK.sendEnderData2C(players, serverPlayer.getUUID());
                  }
            }
      }
}
