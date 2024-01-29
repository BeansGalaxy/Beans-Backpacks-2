package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
      @Inject(method = "dispenseArmor", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
      private static void elytraDispense(BlockSource $$0, ItemStack stack, CallbackInfoReturnable<Boolean> cir, BlockPos $$2, List $$3, LivingEntity entity) {
            if (entity instanceof Player player) {
                  BackData backData = BackData.get(player);
                  ItemStack backStack = backData.getStack();
                  Item item = stack.getItem();
                  if (Constants.DISABLES_BACK_SLOT.contains(item) && !backStack.isEmpty()) {
                        cir.setReturnValue(false);
                  }
                  else if (item instanceof ElytraItem elytraItem && Constants.CHESTPLATE_DISABLED.contains(Items.ELYTRA.asItem()))
                  {
                        if (backStack.isEmpty() && backData.backSlot.mayPlace(stack))
                        {
                              backData.set(stack.split(1));
                              cir.setReturnValue(true);
                              if (!player.level().isClientSide() && !player.isSilent())
                                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), elytraItem.getEquipSound(), player.getSoundSource(), 1.0F, 1.0F);
                        } else
                              cir.setReturnValue(false);

                  }
            }
      }
}
