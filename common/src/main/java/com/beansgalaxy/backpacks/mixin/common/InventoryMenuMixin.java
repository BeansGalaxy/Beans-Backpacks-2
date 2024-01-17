package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackAccessor;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.InSlot;
import com.beansgalaxy.backpacks.core.Kind;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryMenu.class, priority = 899)
public abstract class InventoryMenuMixin extends RecipeBookMenu<TransientCraftingContainer>{

      @Shadow @Final private Player owner;
      @Shadow @Final private static EquipmentSlot[] SLOT_IDS;

      public InventoryMenuMixin(MenuType<?> $$0, int $$1) {
            super($$0, $$1);
      }

      @Inject(method = "<init>", at = @At("TAIL"))
      private void onConstructed(Inventory playerInventory, boolean isServerSide, Player player, CallbackInfo ci) {
            BackData backData = ((BackAccessor) playerInventory).getBackData();
            if (!Constants.SLOTS_MOD_ACTIVE)
            {
                  backData.backSlot.slotIndex = slots.size();
                  this.addSlot(backData.backSlot);
            }
            backData.inSlot.slotIndex = slots.size();
            this.addSlot(backData.inSlot);
      }

      @ModifyArg(method = "<init>", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
      private Slot overrideChestplate(Slot par1) {
            ResourceLocation[] EMPTY_ARMOR_SLOT_TEXTURES = new ResourceLocation[]{ InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
            int index = par1.getContainerSlot();
            final EquipmentSlot equipmentSlot = SLOT_IDS[39 - index];

            return new Slot(par1.container, index, par1.x, par1.y) {

                  @Override
                  public void setByPlayer(ItemStack stack, ItemStack previousStack) {
                        owner.onEquipItem(equipmentSlot, previousStack, stack);
                        super.setByPlayer(stack, previousStack);
                  }

                  @Override
                  public int getMaxStackSize() {
                        return 1;
                  }

                  @Override
                  public boolean mayPlace(ItemStack stack) {
                        boolean conflictsWithBackSlot = Constants.DISABLES_BACK_SLOT.contains(stack.getItem()) && !BackData.get(owner).isEmpty();
                        return equipmentSlot == Mob.getEquipmentSlotForItem(stack) && !Constants.CHESTPLATE_DISABLED.contains(stack.getItem()) && !conflictsWithBackSlot;
                  }

                  @Override
                  public boolean mayPickup(Player playerEntity) {
                        ItemStack itemStack = this.getItem();
                        if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                              return false;
                        }
                        return super.mayPickup(playerEntity);
                  }

                  @Override
                  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                        return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()]);
                  }
            };
      }

      @Inject(method = "quickMoveStack", at = @At("HEAD"))
      private void quickMove(Player player, int slot, CallbackInfoReturnable<ItemStack> cir) {
            BackData backData = ((BackAccessor) player.getInventory()).getBackData();
            ItemStack stack = this.slots.get(slot).getItem();
            if ((Kind.isWearable(stack)) && backData.isEmpty() && backData.backSlot.isActive()) {
                  backData.set(stack.copy());
                  stack.setCount(0);
            }
      }

      @Unique
      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            if (InSlot.continueSlotClick(slotIndex, button, actionType, player))
                  super.clicked(slotIndex, button, actionType, player);
      }
}
