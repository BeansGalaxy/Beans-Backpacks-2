package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.data.config.TooltipType;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

      @Shadow public abstract Item getItem();

      @Unique private final ItemStack instance = ((ItemStack) (Object) this);

      @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(ItemStack stack, Slot slot, ClickAction clickAction, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
            if (BackpackItem.interact(instance, clickAction, player, access, false))
                  cir.setReturnValue(true);
      }

      @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
      private void stackedOnMe(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
            Optional<TooltipComponent> tooltip = Tooltip.get(instance);
            if (!tooltip.equals(Optional.empty()))
                  cir.setReturnValue(tooltip);
      }

      @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                  target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
      private void redirectBackpackTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> components, MutableComponent name) {
            if (player == null)
                  return;

            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            Traits.LocalData traits = Traits.LocalData.fromStack(instance, player);
            Kind instanceKind = traits.kind;
            boolean actionKeyPressed = backData.actionKeyPressed;

            if (traits.isEmpty())
                  return;

            if (instance.is(Services.REGISTRY.getUpgraded()))
            {
                  if (actionKeyPressed) {
                        components.add(Component.translatable("tooltip.beansbackpacks.null0"));
                        components.add(Component.translatable("tooltip.beansbackpacks.null1"));
                        components.add(Component.empty());
                        components.add(Component.translatable("tooltip.beansbackpacks.null2"));
                        components.add(Component.translatable("tooltip.beansbackpacks.null3"));
                        components.add(Component.empty());
                        components.add(Component.translatable("tooltip.beansbackpacks.null4"));
                        components.add(Component.translatable("tooltip.beansbackpacks.null5"));
                        components.add(Component.translatable("tooltip.beansbackpacks.null6"));
                        cir.setReturnValue(components);
                  }
                  else Tooltip.nullTitle(components);;
                  return;
            }

            if (Constants.CLIENT_CONFIG.tooltip_style.get() != TooltipType.INTEGRATED
            && instance == backStack
            && Tooltip.isCuriosMenu()
            && !backData.getBackpackInventory().isEmpty()) {
                  components.clear();
                  components.add(Component.empty());
                  cir.setReturnValue(components);
            } else if (instance == backStack && instance.hasTag() && instance.getTag().contains("back_slot"))
                  cir.setReturnValue(components);
            else if (instanceKind != null)
                  Tooltip.appendTooltip(player, flag, components, traits, instance);
      }

}
