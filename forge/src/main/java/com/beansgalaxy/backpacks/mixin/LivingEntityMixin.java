package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

      @Redirect(method = "updateFallFlying", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public ItemStack getItemBySlot(LivingEntity instance, EquipmentSlot equipmentSlot) {
            if (instance instanceof Player player)
                  return Constants.getTorsoWearables(player, Items.ELYTRA);
            return instance.getItemBySlot(equipmentSlot);
      }
}
