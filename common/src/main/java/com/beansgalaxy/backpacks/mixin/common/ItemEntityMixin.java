package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
      @Shadow public abstract ItemStack getItem();

      @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
      private void backpackItemFireImmune(CallbackInfoReturnable<Boolean> cir) {
            ItemStack stack = this.getItem();
            Traits traits = Kind.getTraits(stack);
            if (!traits.isEmpty() && traits.fireResistant)
                  cir.setReturnValue(true);
      }
}
