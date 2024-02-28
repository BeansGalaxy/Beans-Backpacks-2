package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {

      @Inject(method = "use", cancellable = true, at = @At("HEAD"))
      private void stopDisabledEquipment(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
            ElytraItem item = (ElytraItem) (Object) this;
            ItemStack itemStack = player.getItemInHand(hand);
            BackData backData = BackData.get(player);
            Kind kind = Kind.fromStack(backData.getStack());

            if (!backData.isEmpty() && Constants.elytraOrDisables(item)) {
                  if (level.isClientSide())
                        Tooltip.playSound(kind, PlaySound.HIT);

                  cir.setReturnValue(InteractionResultHolder.fail(itemStack));
            }
            else if (Constants.ELYTRA_ITEMS.contains(item)) {
                  if (backData.isEmpty() && !backData.backSlotDisabled()) {
                        if (!level.isClientSide())
                              player.awardStat(Stats.ITEM_USED.get(item));

                        backData.playEquipSound(itemStack);
                        backData.set(itemStack.copyAndClear());
                        cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()));
                  }
                  else {
                        if (level.isClientSide())
                              Tooltip.playSound(kind, PlaySound.HIT);

                        cir.setReturnValue(InteractionResultHolder.fail(itemStack));
                  }
            }
      }
}
