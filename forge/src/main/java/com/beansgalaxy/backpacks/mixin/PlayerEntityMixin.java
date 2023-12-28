package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerEntityMixin {

      @Redirect(method = "tryToStartFallFlying()Z", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public ItemStack getItemBySlot(Player instance, EquipmentSlot $$0) {
            return Constants.getTorsoWearables(instance, Items.ELYTRA);
      }
}
