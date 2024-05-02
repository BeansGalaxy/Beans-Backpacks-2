package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {

      @Inject(method = "use", cancellable = true, at = @At("HEAD"))
      private void stopDisabledEquipment(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
            ElytraItem item = (ElytraItem) (Object) this;
            ItemStack itemStack = player.getItemInHand(hand);
            BackData backData = BackData.get(player);
            ItemStack stack = backData.getStack();

            if (Kind.isWearableElytra(item)) {
                  ItemStack chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
                  Component msg = null;
                  if (Kind.isWings(chestSlot)) {
                        msg = Component.translatable("entity.beansbackpacks.blocked.already_equipped",
                                    Constants.getName(chestSlot));
                  } else if (backData.isEmpty()) {
                        if (!level.isClientSide())
                              player.awardStat(Stats.ITEM_USED.get(item));

                        backData.playEquipSound(itemStack);
                        backData.set(itemStack.copyAndClear());
                        cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()));
                  } else if (Kind.isWings(stack)) {
                        msg = Component.translatable("entity.beansbackpacks.blocked.already_equipped",
                                    Constants.getName(stack));
                  }

                  if (msg != null) {
                        player.displayClientMessage(msg, true);
                        if (player.level().isClientSide) {
                              Tooltip.playSound(Kind.WINGED, PlaySound.HIT);
                              Tooltip.pushInventoryMessage(msg);
                        }
                        cir.setReturnValue(InteractionResultHolder.fail(itemStack));
                  }
            }
      }
}
