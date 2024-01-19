package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.core.BackAccessor;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.platform.Services;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Inventory.class)
public class InventoryMixin implements BackAccessor {

      @Shadow @Final public Player player;
      @Unique private BackData backData;

      @Override
      @Unique
      public BackData getBackData() {
            if (backData == null)
                  backData = new BackData(player);
            return backData;
      }

      @Inject(method = "save", at = @At("TAIL"))
      public void writeBackSlot(ListTag tag, CallbackInfoReturnable<ListTag> cir) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            if (!backStack.isEmpty()) {
                  CompoundTag compoundTag = new CompoundTag();
                  CompoundTag backItem = new CompoundTag();
                  backStack.save(backItem);
                  compoundTag.put("BackSlot", backItem);
                  if (Kind.isStorage(backStack)) {
                        CompoundTag backTag1 = new CompoundTag();
                        BackpackInventory backpackInventory = backData.backpackInventory;
                        backpackInventory.writeNbt(backTag1, backpackInventory.isEmpty());
                        compoundTag.put("Contents", backTag1);
                  }
                  tag.add(compoundTag);
            }
      }

      @Inject(method = "load", at = @At("TAIL"))
      public void readMixin(ListTag tag, CallbackInfo info) {
            depricatedLoad(tag);
            BackData backData = BackData.get(player);
            for (int i = 0; i < tag.size(); ++i) {
                  CompoundTag compoundTag = tag.getCompound(i);
                  ItemStack itemStack = ItemStack.of(compoundTag.getCompound("BackSlot"));
                  if (!itemStack.isEmpty()) {
                        backData.set(itemStack);
                        if (Kind.isStorage(itemStack))
                              backData.backpackInventory.readStackNbt(compoundTag.getCompound("Contents"));
                  }
            }
      }

      @Unique
      private void depricatedLoad(ListTag tag) {
            BackData backData = BackData.get(player);
            backData.set(ItemStack.EMPTY);
            for (int i = 0; i < tag.size(); ++i) {
                  CompoundTag compoundTag = tag.getCompound(i);
                  int slot = compoundTag.getByte("Slot") & 255;
                  ItemStack itemStack = ItemStack.of(compoundTag);
                  if (!itemStack.isEmpty()) {
                        if (slot == 110) {
                              backData.set(itemStack);
                              if (Kind.isStorage(itemStack))
                                    backData.backpackInventory.readStackNbt(compoundTag.getCompound("Contents"));
                        }
                  }
            }
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
      public void insertInventory(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            Inventory instance = (Inventory) (Object) this;
            if (slot == -1 && !stack.isEmpty())
            {
                  BackData backData = BackData.get(player);
                  BackpackInventory backpackInventory = backData.backpackInventory;

                  if (Kind.isStorage(backData.getStack()) && backpackInventory.canPlaceItem(stack))
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
                        AtomicBoolean tookStack = new AtomicBoolean(false);
                        backpackInventory.getItemStacks().forEach(stacks -> {
                              if (ItemStack.isSameItemSameTags(stacks, stack)) {
                                    backpackInventory.insertItemSilent(stack, stack.getCount());
                                    backpackInventory.setChanged();
                                    tookStack.set(true);
                              }
                        });

                        if (tookStack.get()) {
                              cir.setReturnValue(true);
                              if (player instanceof ServerPlayer serverPlayer) {
                                    Services.NETWORK.backpackInventory2C(serverPlayer);
                              }
                        }
                  }
            }
      }

      @ModifyVariable(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z", shift = At.Shift.BEFORE), argsOnly = true)
      private int remapSlot(int slot) {
            if (slot == -2)
                  return -1;
            else
                  return slot;
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
      public void insertBackpack(int $$0, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            if (!cir.getReturnValue()) {
                  BackpackInventory backpackInventory = BackData.get(player).backpackInventory;
                  cir.setReturnValue(backpackInventory.insertItemSilent(stack, stack.getCount()).isEmpty());
            }
      }
}
