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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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
                  Kind kind = Kind.fromStack(backStack);
                  Item item = stack.getItem();

                  if (backStack.isEmpty()) {
                        if (Kind.isWearable(item)) {
                              backData.set(stack.split(1));
                              cir.setReturnValue(true);
                              if (!player.level().isClientSide() && !player.isSilent())
                                    if (item instanceof Equipable equipable)
                                          player.level().playSound(null, player.getX(), player.getY(), player.getZ(), equipable.getEquipSound(), player.getSoundSource(), 1.0F, 1.0F);
                                    else if (kind != null)
                                          PlaySound.EQUIP.at(player, kind);
                        }
                  } else {
                        if (Constants.elytraOrDisables(item) || Constants.CHESTPLATE_DISABLED.contains(item)) {
                              cir.setReturnValue(false);
                              if (player.level().isClientSide()) {
                                    Tooltip.playSound(kind, PlaySound.HIT);
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
            Kind kind = Kind.fromStack(backData.getStack());

            if (!backData.isEmpty() && Constants.elytraOrDisables(item)) {
                  if (level.isClientSide())
                        Tooltip.playSound(kind, PlaySound.HIT);

                  cir.setReturnValue(InteractionResultHolder.fail(itemStack));
            }
            else if (Constants.CHESTPLATE_DISABLED.contains(item)) {
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
