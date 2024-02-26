package com.beansgalaxy.backpacks.mixin.client;

import com.beansgalaxy.backpacks.core.Kind;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ArmorTrim.class)
public class ArmorTrimMixin {

      @Inject(method = "appendUpgradeHoverText", at = @At("HEAD"), cancellable = true)
      private static void syncEnderTrim(ItemStack itemStack, RegistryAccess registryAccess, List<Component> list, CallbackInfo ci) {
            if (Kind.ENDER.is(itemStack)) {
                  ci.cancel();
            }

      }

}
