package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.inventory.PotInventory;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.network.clientbound.SendBackInventory;
import com.beansgalaxy.backpacks.network.serverbound.PickBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PickBlockEvent {

      public static void pickBackpack(int backpackSlot, ServerPlayer player) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();

            Inventory inventory = player.getInventory();

            int freeSlot = inventory.getFreeSlot();
            if (freeSlot == -1)
            {
                  if (player.level().isClientSide())
                        Tooltip.playSound(Kind.getTraits(backStack).sound, PlaySound.HIT, 0.1f);
                  return;
            }

            if (freeSlot < 9)
                  inventory.selected = freeSlot;

            ItemStack selectedStack = inventory.getItem(inventory.selected);
            BackpackInventory backpackInventory = backData.getBackpackInventory();
            ItemStack take;
            if (backpackSlot != -1 || (take = PotInventory.take(backStack, false, player.level())) == null)
                  take = backpackInventory.removeItemSilent(backpackSlot);

            inventory.setItem(inventory.selected, take);
            backpackInventory.playSound(PlaySound.TAKE);

            int overflowSlot = -1;
            if (!selectedStack.isEmpty())
            {
                  overflowSlot = inventory.getFreeSlot();
                  inventory.setItem(overflowSlot, selectedStack);
            }

            player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, inventory.selected, selectedStack));
            player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));

            SendBackInventory.send(player);
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
            if (backData.getTraits().kind.is(Kind.POT)) {
                  ItemStack backStack = backData.getStack();
                  CompoundTag tag = backStack.getTag();
                  if (tag != null && tag.contains("back_slot")) {
                        CompoundTag backSlot = tag.getCompound("back_slot");
                        if (itemStack.is(PotInventory.getContent(backSlot))) {
                              PotInventory.take(backStack, false, player.level());
                              PickBackpack.send(-1);
                              return true;
                        }
                  }
                  return false;
            }
            slot = PickBlockEvent.slotMatchingItem(itemStack, backData.getBackpackInventory());

            if (slot < 0)
                  return false;

            PickBackpack.send(slot);
            return true;
      }
}
