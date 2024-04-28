package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackSlot;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;

public class CommonAtClient {
      public static boolean syncBackSlot(int entity, ItemStack stack) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level().getEntity(entity) instanceof Player otherPlayer) {
                  BackData backSlot = BackData.get(otherPlayer);
                  backSlot.set(stack);
                  return true;
            }
            return false;
      }

      public static void syncBackInventory(String stacks) {
            CompoundTag tag = stringToNbt(stacks);

            LocalPlayer player = Minecraft.getInstance().player;
            BackpackInventory backpackInventory = BackData.get(player).backpackInventory;
            backpackInventory.readStackNbt(tag);
      }

      public static CompoundTag stringToNbt(String string) {
            try {
                  CompoundTag nbt = NbtUtils.snbtToStructure(string);
                  return nbt;
            } catch (CommandSyntaxException e) {
                  throw new RuntimeException(Constants.MOD_ID + ": Failed to sync BackpackInventory with networking");
            }
      }

      public static void syncViewersPacket(int id, byte viewers) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(id);
            if (entity instanceof EntityAbstract entityAbstract)
                  entityAbstract.viewable.viewers = viewers;
            else if (entity instanceof Player player) {
                  BackData.get(player).backpackInventory.getViewable().viewers = viewers;
            }
      }

      public static void receiveEnderPos(HashSet<EnderStorage.PackagedLocation> enderLocations) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            BackData backSlot = BackData.get(player);
            backSlot.setEnderLocations(enderLocations);
      }

      public static void receiveEquipLockMsg(Component requester) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            MutableComponent keybind = Component.literal(Tooltip.keyBind).withStyle(ChatFormatting.GOLD);
            player.displayClientMessage(Component.translatable("entity.beansbackpacks.equip_locked_msg", keybind, requester), true);
      }
}
