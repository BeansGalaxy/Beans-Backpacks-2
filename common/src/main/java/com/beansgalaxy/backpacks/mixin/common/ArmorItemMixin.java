package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.IConfig;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin implements Equipable {
      @Inject(method = "dispenseArmor", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
      private static void stopDisabledDispense(BlockSource $$0, ItemStack stack, CallbackInfoReturnable<Boolean> cir, BlockPos $$2, List $$3, LivingEntity entity) {
            if (entity instanceof Player player) {
                  BackData backData = BackData.get(player);
                  ItemStack backStack = backData.getStack();
                  Traits traits = Kind.getTraits(backStack);
                  Kind kind = Kind.fromStack(backStack);
                  Item item = stack.getItem();

                  if (backStack.isEmpty()) {
                        if (Kind.isWearable(item)) {
                              backData.set(stack.split(1));
                              cir.setReturnValue(true);
                              if (!player.level().isClientSide() && !player.isSilent())
                                    if (item instanceof Equipable equipable)
                                          player.level().playSound(null, player.getX(), player.getY(), player.getZ(), equipable.getEquipSound(), player.getSoundSource(), 1.0F, 1.0F);
                                    else if (!traits.isEmpty())
                                          PlaySound.EQUIP.at(player, traits.sound);
                        }
                  } else {
                        if (IConfig.cantEquipWithBackpack(item)) {
                              cir.setReturnValue(false);
                              if (player.level().isClientSide()) {
                                    Tooltip.playSound(traits.sound, PlaySound.HIT);
                              }
                        }
                  }
            }
      }

      @Inject(method = "use", cancellable = true, at = @At("HEAD"))
      private void stopDisabledEquipment(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
            ArmorItem item = (ArmorItem) (Object) this;
            ItemStack itemStack = player.getItemInHand(hand);
            BackData backData = BackData.get(player);

            if (IConfig.chestplateDisabled(item)) {
                  if (backData.isEmpty()) {
                        if (!level.isClientSide())
                              player.awardStat(Stats.ITEM_USED.get(item));

                        backData.playEquipSound(itemStack);
                        backData.set(itemStack.copyAndClear());
                        cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()));
                  }
                  else {
                        cir.setReturnValue(InteractionResultHolder.fail(itemStack));
                  }
            }
      }
}
