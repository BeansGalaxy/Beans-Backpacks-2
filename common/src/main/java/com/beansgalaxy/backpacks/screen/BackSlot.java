package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.network.clientbound.EquipLockedMsg;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class BackSlot extends Slot {
      public final BackData backData;
      public int slotIndex = -1;

      public BackSlot(BackData backData) {
            super(new SimpleContainer(1), 0, ServerSave.CONFIG.back_slot_pos.get(0), ServerSave.CONFIG.back_slot_pos.get(1));
            this.backData = backData;
      }

      @Override
      public boolean isActive() {
            return !Constants.SLOTS_MOD_ACTIVE;
      }

      @Override
      public boolean mayPickup(Player player) {
            ItemStack itemStack = backData.getStack();
            boolean backpackIsEmpty = backData.getBackpackInventory().isEmpty();
            boolean standardCheck = itemStack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemStack);
            return standardCheck && backpackIsEmpty;
      }

      @Override
      public boolean isHighlightable() {
            return backData.getBackpackInventory().isEmpty() || getItem().isEmpty();
      }

      @Override
      public int getMaxStackSize() {
            return 1;
      }

      @Override
      public boolean mayPlace(ItemStack stack) {
            return backData.mayEquip(stack);
      }

      @Override
      public void set(ItemStack $$0) {
            backData.update($$0);
            backData.setChanged();
            super.set($$0);
      }

      public static InteractionResult openPlayerBackpackMenu(Player viewer, ServerPlayer owner) {
            BackData backData = BackData.get(owner);
            ItemStack backpackStack = backData.getStack();
            if (!Kind.isBackpack(backpackStack) || backData.backSlotDisabled())
                  return InteractionResult.PASS;

            // CHECKS ROTATION OF BOTH PLAYERS
            boolean yawMatches = BackpackInventory.yawMatches(viewer.yHeadRot, owner.yBodyRot, 90d);

            // OFFSETS OTHER PLAYER'S POSITION
            double angleRadians = Math.toRadians(owner.yBodyRot);
            double offset = -0.3;
            double x = owner.getX();
            double z = owner.getZ();
            double offsetX = Math.cos(angleRadians) * offset;
            double offsetZ = Math.sin(angleRadians) * offset;
            double newX = x - offsetZ;
            double newY = owner.getEyeY() - .45;
            double newZ = z + offsetX;

            // CHECKS IF PLAYER IS LOOKING
            Vec3 vec3d = viewer.getViewVector(1.0f).normalize();
            Vec3 vec3d2 = new Vec3(newX - viewer.getX(), newY - viewer.getEyeY(), newZ - viewer.getZ());
            double d = -vec3d2.length() + 5.65;
            double e = vec3d.dot(vec3d2.normalize());
            double maxRadius = 0.05;
            double radius = (d * d * d * d) / 625;
            boolean looking = e > 1.0 - radius * maxRadius && viewer.hasLineOfSight(owner);

            if (yawMatches && looking) { // INTERACT WITH BACKPACK CODE GOES HERE
                  if (!backData.actionKeyDown && backpackStack.hasTag() && backpackStack.getTag().getBoolean("Locked")) {
                        EquipLockedMsg.send(viewer, owner);
                        PlaySound.HIT.at(owner, backData.getTraits().sound());
                  } else {
                        Services.NETWORK.openBackpackMenu(viewer, backData);
                        backData.getBackpackInventory().playSound(PlaySound.OPEN, 1f);
                  }
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }
}
