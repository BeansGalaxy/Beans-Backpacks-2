package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {
      @Unique
      @Final
      private ElytraItem instance = (ElytraItem) (Object) this;
      
      @Inject(method = "use", at = @At("HEAD"), cancellable = true)
      private void elytraUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
            ItemStack stack = player.getItemInHand(hand);
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();

            if (!Constants.ELYTRA_ITEMS.contains(instance))
                  return;

            if (backData.backSlotDisabled() || !backStack.isEmpty()) {
                  cir.setReturnValue(InteractionResultHolder.fail(stack));
                  if (level.isClientSide())
                        Tooltip.playSound(backData.getLocalData().kind(), PlaySound.HIT);
                  return;
            }

            if (backData.backSlot.mayPickup(player) && !ItemStack.matches(stack, backStack)) {
                  if (!level.isClientSide() && !player.isSilent()) {
                        player.awardStat(Stats.ITEM_USED.get(instance));
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), instance.getEquipSound(), player.getSoundSource(), 1.0F, 1.0F);
                  }

                  ItemStack $$7 = backStack.isEmpty() ? stack : backStack.copyAndClear();
                  ItemStack $$8 = stack.copyAndClear();
                  backData.set($$8);
                  cir.setReturnValue(InteractionResultHolder.sidedSuccess($$7, level.isClientSide()));
            } else {
                  cir.setReturnValue(InteractionResultHolder.fail(stack));
            }
      }
      
}
