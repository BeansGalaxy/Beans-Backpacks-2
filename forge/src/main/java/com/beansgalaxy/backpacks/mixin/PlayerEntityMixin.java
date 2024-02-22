package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {

      @Inject(method = "tryToStartFallFlying()Z", cancellable = true, at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
      public void getItemBySlot(CallbackInfoReturnable<Boolean> cir) {
            Player instance = (Player) (Object) this;
            BackData backData = BackData.get(instance);
            ItemStack backStack = backData.getStack();
            if (Kind.isWings(backStack) && ElytraItem.isFlyEnabled(backStack)) {
                  instance.startFallFlying();
                  cir.setReturnValue(true);
            }
      }
}
