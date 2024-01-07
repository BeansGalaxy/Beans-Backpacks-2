package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.screen.BackSlot;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements Container {

      @Shadow @Final public Player player;

      @Inject(method = "save", at = @At("TAIL"))
      public void writeBackSlot(ListTag tag, CallbackInfoReturnable<ListTag> cir) {
            BackSlot backSlot = BackSlot.get(player);
            if (!backSlot.getItem().isEmpty()) {
                  CompoundTag compoundTag = new CompoundTag();
                  compoundTag.putByte("Slot", (byte) (110));
                  ItemStack backItem = backSlot.getItem();
                  backItem.save(compoundTag);
                  if (Kind.isStorage(backItem)) {
                        CompoundTag backTag1 = new CompoundTag();
                        BackpackInventory backpackInventory = BackSlot.getInventory(player);
                        backpackInventory.writeNbt(backTag1, backpackInventory.isEmpty());
                        compoundTag.put("Contents", backTag1);
                  }
                  tag.add(compoundTag);
            }
      }

      @Inject(method = "load", at = @At("TAIL"))
      public void readMixin(ListTag tag, CallbackInfo info) {
            BackSlot backSlot = BackSlot.get(player);
            backSlot.set(ItemStack.EMPTY);
            for (int i = 0; i < tag.size(); ++i) {
                  CompoundTag compoundTag = tag.getCompound(i);
                  int slot = compoundTag.getByte("Slot") & 255;
                  ItemStack itemStack = ItemStack.of(compoundTag);
                  if (!itemStack.isEmpty()) {
                        if (slot == 110) {
                              backSlot.set(itemStack);
                              if (Kind.isStorage(itemStack))
                                    BackSlot.getInventory(player).readStackNbt(compoundTag.getCompound("Contents"));
                        }
                  }
            }
      }

      @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
      public void insertInventory(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            Inventory instance = (Inventory) (Object) this;
            if (slot == -1 && !stack.isEmpty())
            {
                  BackSlot backSlot = BackSlot.get(player);
                  BackpackInventory backpackInventory = BackSlot.getInventory(player);

                  if (Kind.isStorage(backSlot.getItem()) && backpackInventory.canPlaceItem(stack))
                  {
                        instance.items.forEach(stacks -> {
                              if (stacks.is(stack.getItem())) {
                                    int present = stacks.getCount();
                                    int inserted = stack.getCount();
                                    int count = present + inserted;
                                    int remainder = Math.max(0, count - stack.getMaxStackSize());
                                    count -= remainder;

                                    stacks.setCount(count);
                                    stack.setCount(remainder);
                              }
                        });

                        backpackInventory.getItemStacks().forEach(stacks -> {
                              if (stacks.is(stack.getItem())) {
                                    backpackInventory.insertItemSilent(stack, stack.getCount());
                                    backpackInventory.setChanged();
                              }
                        });

                        if (stack.isEmpty())
                              cir.setReturnValue(true);
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
                  BackpackInventory backpackInventory = BackSlot.getInventory(player);
                  cir.setReturnValue(backpackInventory.insertItemSilent(stack, stack.getCount()).isEmpty());
            }
      }
}
