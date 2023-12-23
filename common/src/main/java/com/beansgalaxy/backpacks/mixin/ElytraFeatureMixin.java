package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraLayer.class)
public class ElytraFeatureMixin {
      @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public ItemStack render(LivingEntity instance, EquipmentSlot equipmentSlot) {
            if (instance instanceof Player player) {
                  Slot backSlot = BackSlot.get(player);
                  return backSlot.getItem();
            }
            return instance.getItemBySlot(EquipmentSlot.CHEST);
      }
}
