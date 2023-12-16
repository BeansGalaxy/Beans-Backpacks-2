package com.beansgalaxy.backpacks.mixin;

import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.screen.BackSlot;
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
import org.spongepowered.asm.mixin.injection.Redirect;
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

      @Redirect(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/player/Inventory;add(ILnet/minecraft/world/item/ItemStack;)Z"))
      public boolean insertStack(Inventory instance, int slot, ItemStack stack) {
            return BackSlot.get(instance.player).pickupItemEntity(instance, stack);
      }

}
