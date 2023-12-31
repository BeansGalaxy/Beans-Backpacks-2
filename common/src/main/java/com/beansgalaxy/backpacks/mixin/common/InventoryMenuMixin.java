package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.screen.BackSlot;
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

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<TransientCraftingContainer> {

      @Shadow @Final private Player owner;
      @Shadow @Final private static EquipmentSlot[] SLOT_IDS;
      @Unique private static final ResourceLocation INPUT = new ResourceLocation("sprites/empty_slot_input");

      public InventoryMenuMixin(MenuType<?> $$0, int $$1) {
            super($$0, $$1);
      }

      @Inject(method = "<init>", at = @At("TAIL"))
      private void onConstructed(Inventory playerInventory, boolean isServerSide, Player player, CallbackInfo ci) {
            BackSlot.SLOT_INDEX = slots.size();
            BackSlot backSlot = new BackSlot(0, 59, 62, player);
            this.addSlot(backSlot);
            this.addSlot(new Slot(backSlot.backpackInventory, 0,59, 45) {

                  @Override
                  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                        return Pair.of(BackSlot.BACKPACK_ATLAS, INPUT);
                  }

                  @Override
                  public boolean isActive() {
                        ItemStack stack = BackSlot.get(player).getItem();
                        boolean b = !backSlot.backpackInventory.isEmpty() || Kind.isStorage(stack);
                        return b && !player.isCreative();
                  }

                  public boolean mayPlace(ItemStack stack) {
                        return backSlot.backpackInventory.canPlaceItem(stack);
                  }

            });
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
                        boolean conflictsWithBackSlot = Constants.DISABLES_BACK_SLOT.contains(stack.getItem()) && !BackSlot.get(owner).getItem().isEmpty();
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
            Slot backSlot = this.slots.get(BackSlot.SLOT_INDEX);
            ItemStack stack = this.slots.get(slot).getItem();
            if ((Kind.isWearable(stack)) && !backSlot.hasItem() && backSlot.isActive()) {
                  backSlot.set(stack.copy());
                  stack.setCount(0);
            }
      }

      @Unique
      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            if (BackSlot.continueSlotClick(slotIndex, button, actionType, player))
                  super.clicked(slotIndex, button, actionType, player);
      }
}
