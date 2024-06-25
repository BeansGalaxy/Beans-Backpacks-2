package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.BackData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
      public ExperienceOrbMixin(EntityType<?> $$0, Level $$1) {
            super($$0, $$1);
      }

      @Shadow protected abstract int xpToDurability(int i);

      @Shadow protected abstract int durabilityToXp(int i);

      @Shadow protected abstract int repairPlayerItems(Player player, int i);

      @Shadow private int value;

      @Inject(method = "repairPlayerItems", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD,
                  at = @At(value = "HEAD", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
      private void repairBackSlotWithArmor(Player player, int xp, CallbackInfoReturnable<Integer> cir) {
            ItemStack backStack = BackData.get(player).getStack();
            if (backStack.isDamaged()) {
                  int i = 0;
                  for (ItemStack itemStack : player.getInventory().armor) {
                        if (!itemStack.isEmpty())
                              i++;
                  }

                  if (i == 0 || level().getRandom().nextInt(i) == 0) {
                        int returnInt = beans_Backpacks_2(player, xp, backStack);
                        cir.setReturnValue(returnInt);
                  }
            }
      }

      @Unique
      private int beans_Backpacks_2(Player player, int xp, ItemStack backStack) {
            int repairAmount = Math.min(xpToDurability(value), backStack.getDamageValue());
            backStack.setDamageValue(backStack.getDamageValue() - repairAmount);

            int xpLeft = xp - durabilityToXp(repairAmount);
            return xpLeft > 0 ? repairPlayerItems(player, xpLeft) : 0;
      }
}
