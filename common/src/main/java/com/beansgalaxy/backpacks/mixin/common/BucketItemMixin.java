package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.access.BucketAccess;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BucketItem.class)
public class BucketItemMixin implements BucketAccess {

      @Shadow @Final private Fluid content;

      @Override
      public Fluid beans_Backpacks_2$getFluid() {
            return content;
      }
}
