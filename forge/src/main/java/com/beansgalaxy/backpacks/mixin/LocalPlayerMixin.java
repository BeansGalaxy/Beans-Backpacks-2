package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

      @Redirect(method = "aiStep", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public ItemStack getItemBySlot(LocalPlayer instance, EquipmentSlot equipmentSlot) {
            return Constants.getTorsoWearables(instance, Items.ELYTRA);
      }

}
