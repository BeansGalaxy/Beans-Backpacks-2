package com.beansgalaxy.backpacks.client.network;

import com.beansgalaxy.backpacks.access.MinecraftAccessor;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.KeyPress;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.items.Tooltip;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderSound;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import java.util.*;
import java.util.function.Consumer;

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

      public static boolean syncBackInventory(CompoundTag tag, int container) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return container > -1;

            if (container > -1) {
                  AbstractContainerMenu containerMenu = player.containerMenu;
                  if (containerMenu.containerId == container && containerMenu instanceof BackpackMenu menu) {
                        menu.backpackInventory.readStackNbt(tag);
                  }
                  return true;
            }

            BackData backData = BackData.get(player);
            BackpackInventory backpackInventory = backData.getBackpackInventory();
            backpackInventory.readStackNbt(tag);
            return true;
      }

      public static void syncViewersPacket(int id, byte viewers) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(id);
            if (entity instanceof EntityAbstract entityAbstract)
                  entityAbstract.getViewable().setViewers(viewers);
            else if (entity instanceof Player player) {
                  BackData.get(player).getBackpackInventory().getViewable().setViewers(viewers);
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

            MutableComponent keybind = KeyPress.getReadable(false).plainCopy().withStyle(ChatFormatting.GOLD);
            player.displayClientMessage(Component.translatable("entity.beansbackpacks.equip_locked_msg", keybind, requester), true);
      }

      public static EnderStorage getEnderStorage() {
            MinecraftAccessor accessor = (MinecraftAccessor) Minecraft.getInstance();
            return accessor.beans_Backpacks_2$getEnder();

      }

      public static void sendEnderData(UUID uuid, Consumer<EnderInventory> consumer) {
            ClientLevel level = Minecraft.getInstance().level;
            EnderInventory computed = EnderStorage.get(level).MAP.computeIfAbsent(uuid, in -> new EnderInventory(uuid, level));
            consumer.accept(computed);
      }

      public static void receiveEnderSoundEvent(SendEnderSound ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) return;

            boolean noMatch = true;
            if (minecraft.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof EntityEnder ender) {
                  BlockPos containing = BlockPos.containing(ender.position());
                  Iterator<BlockPos> iterator = ctx.posList.iterator();
                  while (iterator.hasNext() && noMatch) {
                        BlockPos pos = iterator.next();
                        if (pos.equals(containing)) {
                              PlaySound.Playable play = ctx.sound.getSound(Traits.Sound.VWOOMP);
                              float vol = (float) (ctx.volume + ctx.volume + play.volume()) / 3;
                              level.playSound(minecraft.player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, play.event(), SoundSource.BLOCKS, vol, play.pitch());
                              ctx.volume = ctx.volume - Math.abs(ctx.volume - vol);

                              ctx.posList.remove(pos);
                              noMatch = false;
                        }
                  }
            }

            SendEnderSound.soundQue.add(ctx);
            if (noMatch) SendEnderSound.indexSounds(minecraft.player);
      }
}
