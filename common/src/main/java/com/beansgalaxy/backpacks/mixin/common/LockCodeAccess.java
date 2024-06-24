package com.beansgalaxy.backpacks.mixin.common;

import net.minecraft.world.LockCode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LockCode.class)
public interface LockCodeAccess {
      @Accessor("key")
      String getKey();

}
