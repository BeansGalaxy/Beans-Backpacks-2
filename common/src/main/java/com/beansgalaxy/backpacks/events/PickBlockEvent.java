package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PickBlockEvent {

      public static void pickBackpack(int backpackSlot, ServerPlayer player) {
            BackData backData = BackData.get(player);
            Inventory inventory = player.getInventory();

            Kind kind = Kind.fromStack(backData.getStack());
            int freeSlot = inventory.getFreeSlot();
            if (freeSlot == -1)
            {
                  if (player.level().isClientSide())
                        Tooltip.playSound(kind, PlaySound.HIT, 0.1f);
                  return;
            }

            if (freeSlot < 9)
                  inventory.selected = freeSlot;

            ItemStack selectedStack = inventory.getItem(inventory.selected);
            inventory.setItem(inventory.selected, backData.backpackInventory.removeItemSilent(backpackSlot));
            PlaySound.TAKE.at(player, kind);

            int overflowSlot = -1;
            if (!selectedStack.isEmpty())
            {
                  overflowSlot = inventory.getFreeSlot();
                  inventory.setItem(overflowSlot, selectedStack);
            }

            player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, inventory.selected, selectedStack));
            player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));

            Services.NETWORK.backpackInventory2C(player);
            Services.REGISTRY.triggerSpecial(player, SpecialCriterion.Special.PICK_BACKPACK);

            if (overflowSlot > -1)
                  player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, overflowSlot, inventory.getItem(overflowSlot)));
      }

      public static int slotMatchingItem(ItemStack stack, BackpackInventory backpackInventory) {
            for (int j = 0; j < backpackInventory.getContainerSize(); j++) {
                  ItemStack backpackStack = backpackInventory.getItemStacks().get(j);
                  if (ItemStack.isSameItemSameTags(stack, backpackStack)) {
                        return j;
                  }
            }
            return -1;
      }

      public static boolean cancelPickBlock(boolean instantBuild, Inventory inventory, ItemStack itemStack, Player player) {
            if (instantBuild)
                  return false;

            int slot = inventory.findSlotMatchingItem(itemStack);
            if (slot > -1 || player == null)
                  return false;

            BackData backData = BackData.get(player);
            slot = PickBlockEvent.slotMatchingItem(itemStack, backData.backpackInventory);

            if (slot < 0)
                  return false;

            Services.NETWORK.pickFromBackpack2S(slot);
            return true;
      }
}
