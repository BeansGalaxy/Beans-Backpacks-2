package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

      @Shadow @Final @Deprecated @Nullable private Item item;

      @Shadow public abstract Item getItem();

      @Shadow public abstract ItemStack copy();

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
            Kind kind = Kind.fromStack(backStack);

            if (instance == backStack && kind != null) {
                  boolean actionKeyPressed = backData.actionKeyPressed;
                  boolean hasItems = !backData.backpackInventory.isEmpty();
                  switch (kind) {
                        case POT -> {
                              CompoundTag potTag = backStack.getTagElement("back_slot");
                              if (potTag != null && potTag.contains("id") && potTag.contains("amount")) {
                                    String string = potTag.getString("id");
                                    Item item1 = BuiltInRegistries.ITEM.get(new ResourceLocation(string));
                                    Component name1 = item1.getName(item1.getDefaultInstance());
                                    int amount = potTag.getInt("amount");
                                    MutableComponent literal = Component.literal(amount + " ");
                                    MutableComponent append = literal.append(name1);
                                    components.add(append.withStyle(ChatFormatting.GRAY));
                              } else if (actionKeyPressed)
                                    cir.setReturnValue(Tooltip.addLore(components, "pot", 7));
                              else
                                    Tooltip.loreTitle(components);
                        }
                        case CAULDRON -> {
                              CauldronInventory.Attributes attributes = CauldronInventory.Attributes.create(backStack);
                              if (attributes != null) {
                                    int scale = attributes.access().scale();
                                    MutableComponent literal = Component.literal((attributes.amount() / scale) + " ");
                                    MutableComponent append = literal.append(Component.translatable(attributes.bucket().getDescriptionId()));
                                    components.add(append.withStyle(ChatFormatting.GRAY));
                              } else if (actionKeyPressed)
                                    cir.setReturnValue(Tooltip.addLore(components, "cauldron", 7));
                              else
                                    Tooltip.loreTitle(components);
                        }
                        case ENDER -> {
                              if (hasItems) return;

                              EnderBackpack enderBackpack = (EnderBackpack) getItem();
                              UUID uuid = enderBackpack.getOrCreateUUID(player.getUUID(), instance);
                              Level level = player.level();
                              EnderStorage.Data enderData = EnderStorage.getEnderData(uuid, level);
                              MutableComponent playerName = enderData.getPlayerNameColored(level.registryAccess());

                              if (actionKeyPressed)
                                    cir.setReturnValue(Tooltip.addLoreEnder(components, playerName));
                              else {
                                    Tooltip.loreTitle(components);
                                    components.add(Component.translatableWithFallback("tooltip.beansbackpacks.ender.binding", "§7Bound to: ", playerName));
                              }
                        }
                        default -> {
                              if (hasItems) return;

                              if (actionKeyPressed)
                                    cir.setReturnValue(Tooltip.addLore(components, "backpack", 5));
                              else
                                    Tooltip.loreTitle(components);
                        }
                  }
            } else if (Kind.ENDER.is(kind))
            {
                  EnderBackpack enderBackpack = (EnderBackpack) getItem();
                  UUID uuid = enderBackpack.getOrCreateUUID(player.getUUID(), instance);
                  Level level = player.level();
                  EnderStorage.Data enderData = EnderStorage.getEnderData(uuid, level);
                  MutableComponent playerName = enderData.getPlayerNameColored(level.registryAccess());
                  components.add(Component.translatableWithFallback("tooltip.beansbackpacks.ender.binding", "§7Bound to: ", playerName));
            }
      }

}
