package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BackSlot extends Slot {
      private static final ResourceLocation SLOT_BACKPACK = new ResourceLocation("sprites/empty_slot_backpack");
      private static final ResourceLocation SLOT_ELYTRA = new ResourceLocation("sprites/empty_slot_elytra");
      public final BackData backData;
      public int slotIndex = -1;

      public BackSlot(BackData backData) {
            super(new SimpleContainer(1), 0, BackData.UV[0], BackData.UV[1]);
            this.backData = backData;
      }

      @Override
      public boolean isActive() {
            return !Constants.SLOTS_MOD_ACTIVE && !backData.backSlotDisabled();
      }

      @Override
      public boolean mayPickup(Player player) {
            ItemStack itemStack = backData.getStack();
            boolean backpackIsEmpty = backData.backpackInventory.isEmpty();
            boolean standardCheck = itemStack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemStack);
            return standardCheck && backpackIsEmpty;
      }

      @Override
      public boolean mayPlace(ItemStack stack) {
            return Kind.isWearable(stack) && !backData.backSlotDisabled();
      }

      @Override
      public void setChanged() {
            backData.update(this.getItem());
            super.setChanged();
      }

      public static List<ResourceLocation> getTextures() {
            if (Constants.ELYTRA_ITEMS.contains(Items.ELYTRA.asItem())
                  && Minecraft.getInstance().getConnection().getAdvancements().getAdvancements()
                        .get(ResourceLocation.tryParse("end/root")) != null)
            {
                  return List.of(SLOT_ELYTRA, SLOT_BACKPACK);
            }

            return List.of(SLOT_BACKPACK);
      }

      public static InteractionResult openPlayerBackpackMenu(Player viewer, Player owner) {
            ItemStack backpackStack = BackData.get(owner).getStack();
            if (!Kind.isBackpack(backpackStack))
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
                  Services.NETWORK.openBackpackMenu(viewer, owner);

                  PlaySound.OPEN.at(owner, Kind.fromStack(backpackStack));
                  return InteractionResult.sidedSuccess(!viewer.level().isClientSide);
            }

            return InteractionResult.PASS;
      }
}
