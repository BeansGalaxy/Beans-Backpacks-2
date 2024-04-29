package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.access.MinecraftAccessor;
import com.beansgalaxy.backpacks.data.EnderStorage;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftAccessor {
      @Unique public final EnderStorage beans_Backpacks_2$enderStorage = new EnderStorage();

      @Override
      public EnderStorage beans_Backpacks_2$getEnder() {
            return beans_Backpacks_2$enderStorage;
      }
}
