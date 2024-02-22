package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.events.ElytraEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

      public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
            super(p_19870_, p_19871_);
      }

      @Inject(method = "updateFallFlying", cancellable = true, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public void getItemBySlot(CallbackInfo ci) {
            LivingEntity instance = (LivingEntity) (Object) this;
            if (instance instanceof Player player && ElytraEvent.doesFlyFall(true, player)) {
                  if (!level().isClientSide())
                        this.setSharedFlag(7, true);
                  ci.cancel();
            }
      }
}
