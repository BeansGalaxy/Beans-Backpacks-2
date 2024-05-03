package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.access.BackAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.network.clientbound.SendBackInventory;
import com.beansgalaxy.backpacks.inventory.PotInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Inventory.class)
public abstract class InventoryContainerMixin implements BackAccessor {

      @Shadow @Final public Player player;
      @Shadow @Final public NonNullList<ItemStack> items;

      @Shadow public abstract boolean add(int i, ItemStack itemStack);

      @Unique private BackData backData;

      @Override
      @Unique
      public BackData getBackData() {
            if (backData == null) {
                  backData = new BackData(player);
            }
            return backData;
      }

      @Inject(method = "save", at = @At("TAIL"))
      public void writeBackSlot(ListTag tag, CallbackInfoReturnable<ListTag> cir) {
            BackData backData = getBackData();
            ItemStack backStack = backData.getStack();
            if (!backStack.isEmpty()) {
                  CompoundTag compoundTag = new CompoundTag();
                  CompoundTag backItem = new CompoundTag();
                  backStack.save(backItem);
                  compoundTag.put("BackSlot", backItem);
                  Traits.LocalData traits = backData.getTraits();
                  BackpackInventory backpackInventory = backData.getBackpackInventory();
                  if (!backpackInventory.isEmpty() && !Kind.ENDER.is(traits.kind)) {
                        CompoundTag backTag1 = new CompoundTag();
                        backpackInventory.writeNbt(backTag1);
                        compoundTag.put("Contents", backTag1);
                  }
                  tag.add(compoundTag);
            }
      }

      @Inject(method = "load", at = @At("TAIL"))
      public void readMixin(ListTag tag, CallbackInfo info) {
            depricatedLoad(tag);
            for (int i = 0; i < tag.size(); ++i) {
                  CompoundTag compoundTag = tag.getCompound(i);
                  ItemStack itemStack = ItemStack.of(compoundTag.getCompound("BackSlot"));
                  if (!itemStack.isEmpty()) {
                        BackData backData = getBackData();
                        backData.set(itemStack);
                        Traits.LocalData traits = backData.getTraits();
                        if (traits.isStorage() && !Kind.ENDER.is(traits.kind))
                              backData.getBackpackInventory().readStackNbt(compoundTag.getCompound("Contents"));
                  }
            }
      }

      @Unique
      private void depricatedLoad(ListTag tag) { // TODO: REMOVE THIS LOAD BEFORE RELEASE
            BackData backData = getBackData();
            backData.set(ItemStack.EMPTY);
            for (int i = 0; i < tag.size(); ++i) {
                  CompoundTag compoundTag = tag.getCompound(i);
                  int slot = compoundTag.getByte("Slot") & 255;
                  ItemStack itemStack = ItemStack.of(compoundTag);
                  if (!itemStack.isEmpty()) {
                        if (slot == 110) {
                              backData.set(itemStack);
                              if (backData.getTraits().isStorage())
                                    backData.getBackpackInventory().readStackNbt(compoundTag.getCompound("Contents"));
                        }
                  }
            }
      }

      @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
      public void checkCauldronPotItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            Kind kind = Kind.fromStack(stack);
            if (Kind.is(kind, Kind.POT, Kind.CAULDRON)) {
                  CompoundTag backTag = stack.getTagElement("back_slot");
                  if (backTag != null) {
                        BackData backData = getBackData();
                        if (backData.getStack().isEmpty()) {
                              backData.set(stack.copyAndClear());
                              cir.setReturnValue(true);
                        }
                        else cir.setReturnValue(false);
                  }
            }
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
      public void insertInventory(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            Inventory instance = (Inventory) (Object) this;
            if (slot == -1 && !stack.isEmpty())
            {
                  BackData backData = getBackData();
                  BackpackInventory backpackInventory = backData.getBackpackInventory();
                  ItemStack backStack = backData.getStack();
                  Traits.LocalData traits = backData.getTraits();

                  if (Kind.POT.is(traits.kind))
                  {
                        CompoundTag backTag = backStack.getTagElement("back_slot");
                        if (backTag == null || !backTag.contains("id") || !backTag.contains("amount"))
                              return;

                        instance.items.forEach(stacks -> {
                              if (ItemStack.isSameItemSameTags(stacks, stack)) {
                                    int present = stacks.getCount();
                                    int inserted = stack.getCount();
                                    int count = present + inserted;
                                    int remainder = Math.max(0, count - stack.getMaxStackSize());
                                    count -= remainder;

                                    stacks.setCount(count);
                                    stack.setCount(remainder);
                              }
                        });

                        if (stack.isEmpty()) {
                              cir.setReturnValue(true);
                              return;
                        }

                        ItemStack add = PotInventory.add(backStack, stack, player);
                        if (add != null) cir.setReturnValue(true);
                  }
                  else if (traits.isStorage() && backpackInventory.canPlaceItem(stack))
                  {
                        instance.items.forEach(stacks -> {
                              if (ItemStack.isSameItemSameTags(stacks, stack)) {
                                    int present = stacks.getCount();
                                    int inserted = stack.getCount();
                                    int count = present + inserted;
                                    int remainder = Math.max(0, count - stack.getMaxStackSize());
                                    count -= remainder;

                                    stacks.setCount(count);
                                    stack.setCount(remainder);
                              }
                        });
                        AtomicBoolean didTake = new AtomicBoolean(false);
                        backpackInventory.getItemStacks().forEach(stacks -> {
                              if (ItemStack.isSameItemSameTags(stacks, stack)) {
                                    backpackInventory.insertItemSilent(stack, stack.getCount(), 0);
                                    backpackInventory.setChanged();
                                    didTake.set(true);
                              }
                        });

                        if (didTake.get()) {
                              cir.setReturnValue(true);
                              if (player instanceof ServerPlayer serverPlayer) {
                                    SendBackInventory.send(serverPlayer);
                              }
                        }
                  }
            }
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", cancellable = true,
                  at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"))
      private void remapSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            if (slot == -2) {
                  cir.setReturnValue(add(-1, stack));
            }
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
      public void insertBackpack(int $$0, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            if (!cir.getReturnValue()) {
                  BackData backData = getBackData();
                  BackpackInventory backpackInventory = backData.getBackpackInventory();
                  Traits.LocalData traits = backData.getTraits();
                  if (Kind.is(traits.kind, Kind.POT, Kind.CAULDRON))
                        return;

                  if (!traits.isStorage())
                        return;

                  if (backpackInventory.insertItemSilent(stack, stack.getCount(), 0).isEmpty()) {
                        cir.setReturnValue(true);
                        if (player instanceof ServerPlayer serverPlayer) {
                              SendBackInventory.send(serverPlayer);
                        }
                  }
            }
      }
}
