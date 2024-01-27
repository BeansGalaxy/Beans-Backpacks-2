package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
            if (Constants.CHESTPLATE_DISABLED.contains(Items.ELYTRA.asItem()) && entity instanceof Player player && stack.getItem() instanceof ElytraItem item) {
                  BackData backData = BackData.get(player);
                  if (backData.getStack().isEmpty() && backData.backSlot.mayPlace(stack))
                  {
                        backData.set(stack.split(1));
                        cir.setReturnValue(true);
                        if (!player.level().isClientSide() && !player.isSilent())
                              player.level().playSound(null, player.getX(), player.getY(), player.getZ(), item.getEquipSound(), player.getSoundSource(), 1.0F, 1.0F);
                  }
                  cir.setReturnValue(false);
            }

      }
}
