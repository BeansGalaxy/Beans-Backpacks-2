package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderPos;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderStacks;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class EnderStorage {
      public final HashMap<UUID, EnderInventory> MAP = new HashMap<>();
      private final HashMap<UUID, HashSet<Entity>> VIEWERS = new HashMap<>();

      public static EnderStorage get(Level level) {
            if (level instanceof ServerLevel serverLevel) {
                  MinecraftServer server = serverLevel.getServer();
                  ServerSave save = ServerSave.getSave(server, false);
                  return save.enderStorage;
            } else {
                  return CommonAtClient.getEnderStorage();
            }
      }

      public void addViewer(UUID owner, Entity viewer) {
            if (owner == null || viewer == null) return;

            if (viewer instanceof ServerPlayer)
                  VIEWERS.computeIfAbsent(owner, uuid -> new HashSet<>()).add(viewer);
            else if (viewer instanceof EntityEnder && !viewer.level().isClientSide())
                  VIEWERS.computeIfAbsent(owner, uuid -> new HashSet<>()).add(viewer);
      }

      public void syncViewers(UUID owner) {
            if (owner == null) return;

            HashSet<Entity> inventories = VIEWERS.get(owner);
            if (inventories == null) return;

            Iterator<Entity> iterator = inventories.iterator();
            while (iterator.hasNext()) {
                  Entity viewer = iterator.next();
                  if (viewer instanceof ServerPlayer player) {
                        BackData backData = BackData.get(player);
                        ItemStack backStack = backData.getStack();
                        if (backStack.getItem() instanceof EnderBackpack item) {
                              UUID uuid = item.getOrCreateUUID(owner, viewer.level(), backStack);
                              if (uuid.equals(owner)) {
                                    for (ServerPlayer subViewer : backData.getBackpackInventory().getPlayersViewing())
                                          SendEnderStacks.send(subViewer, owner);
                                    SendEnderStacks.send(player, owner);
                              }
                        }
                  }
                  else if (viewer instanceof EntityEnder ender && ender.level() instanceof ServerLevel serverLevel && !ender.isRemoved()) {
                        UUID placedBy = ender.getPlacedBy();
                        if (placedBy.equals(owner)) {
                              NonNullList<ServerPlayer> playersViewing = ender.getInventory().getPlayersViewing();
                              getEnderData(owner, serverLevel).flagForUpdate(serverLevel);
                              for (ServerPlayer player : playersViewing) {
                                    SendEnderStacks.send(player, owner);
                                    if (player.containerMenu instanceof BackpackMenu menu)
                                          menu.updateSlots();
                              }
                        }
                  }
                  else iterator.remove();
            }

            if (inventories.isEmpty())
                  VIEWERS.remove(owner);
      }

      public void removeViewer(UUID owner, Entity viewer) {
            HashSet<Entity> inventories = VIEWERS.get(owner);
            if (inventories == null) return;

            inventories.remove(viewer);
      }

      public static EnderInventory getEnderData(Player player) {
            return getEnderData(player.getUUID(), player.level());
      }

      public static EnderInventory getEnderData(UUID uuid, Level level) {
            EnderInventory enderData = EnderStorage.get(level).MAP.computeIfAbsent(uuid, uuid1 -> new EnderInventory(uuid, level));
            Player player = level.getPlayerByUUID(uuid);

            if (player != null)
                  enderData.setPlayerName(player.getName().copy());

            return enderData;
      }

      public static class Location {
            public final BlockPos location;
            private final ResourceKey<Level> dimension;

            public Location(BlockPos location, ResourceKey<Level> dimension) {
                  this.location = location;
                  this.dimension = dimension;
            }

            public static void update(UUID uuid, ServerLevel serverLevel) {
                  ServerPlayer owner = (ServerPlayer) serverLevel.getPlayerByUUID(uuid);
                  if (owner == null) return;
                  BackData backData = BackData.get(owner);
                  backData.setEnderLocations(asPackaged(owner.getUUID(), serverLevel));
                  SendEnderPos.send(owner, backData);

            }

            public static HashSet<PackagedLocation> asPackaged(UUID owner, ServerLevel level) {
                  EnderInventory enderData = getEnderData(owner, level);

                  HashSet<PackagedLocation> locations = new HashSet<>();
                  for (UUID backpack : enderData.locations.keySet()) {

                        Location location = enderData.locations.get(backpack);
                        ResourceKey<Level> dimension = location.dimension;
                        BlockPos blockPos = location.location;

                        if (level == null) {
                              locations.add(new PackagedLocation(false, blockPos, dimension));
                              continue;
                        }

                        Entity entity = level.getEntity(backpack);
                        if (entity == null) {
                              locations.add(new PackagedLocation(false, blockPos, dimension));
                              continue;
                        }

                        if (entity.isRemoved()) {
                              enderData.locations.remove(backpack);
                              continue;
                        }

                        BlockPos newBlockPos = entity.blockPosition();
                        enderData.locations.put(backpack, new Location(newBlockPos, dimension));
                        locations.add(new PackagedLocation(true, newBlockPos, dimension));
                  }
                  return locations;
            }

            public void toNBT(CompoundTag tag) {
                  int[] pos = {location.getX(), location.getY(), location.getZ()};
                  tag.putIntArray("BlockPos", pos);
                  ResourceLocation dimension = this.dimension.location();
                  tag.putString("namespace", dimension.getNamespace());
                  tag.putString("path", dimension.getPath());

            }

            public Location(CompoundTag tag) {
                  int[] pos = tag.getIntArray("BlockPos");
                  this.location = new BlockPos(pos[0], pos[1], pos[2]);
                  ResourceLocation r = new ResourceLocation(tag.getString("namespace"), tag.getString("path"));
                  this.dimension = ResourceKey.create(Registries.DIMENSION, r);
            }
      }

      public static class PackagedLocation {
            private final boolean isAccurate;
            private final BlockPos location;
            private final ResourceLocation dimension;

            public PackagedLocation(boolean accurate, BlockPos location, ResourceKey<Level> dimension) {
                  this.isAccurate = accurate;
                  this.location = location;
                  this.dimension = dimension.location();
            }

            public MutableComponent toComponent() {
                  MutableComponent literal = Component.literal("");

                  int x = location.getX();
                  formatCord(x, literal);
                  literal.append(", ");
                  int y = location.getY();
                  formatCord(y, literal);
                  literal.append(", ");
                  int z = location.getZ();
                  formatCord(z, literal);

                  literal.append(" " + dimension.toShortLanguageKey());

                  ChatFormatting color = isAccurate ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
                  return literal.withStyle(color);
            }

            private static void formatCord(int cord, MutableComponent literal) {
                  String number = String.valueOf(cord);
                  int length = number.length();
                  while (length < 3) {
                        literal.append(" ");
                        length++;
                  }
                  literal.append(number);
            }

            public void writeBuf(FriendlyByteBuf buf) {
                  buf.writeBoolean(isAccurate);
                  buf.writeBlockPos(location);
                  buf.writeResourceLocation(dimension);
            }

            public PackagedLocation(FriendlyByteBuf buf) {
                  this.isAccurate = buf.readBoolean();
                  this.location = buf.readBlockPos();
                  this.dimension = buf.readResourceLocation();
            }
      }

      public void toNBT(@NotNull CompoundTag tag) {
            CompoundTag enderTag = new CompoundTag();

            MAP.forEach(((uuid, enderData) -> {
                  if (uuid != null) {
                        CompoundTag data = new CompoundTag();
                        BackpackInventory.writeNbt(data, enderData.getItemStacks());
                        data.put("Trim", enderData.getTrim());
                        data.putString("player_name", Component.Serializer.toJson(enderData.getPlayerName()));

                        CompoundTag locations = new CompoundTag();
                        for (UUID backpack : enderData.locations.keySet()) {
                              CompoundTag location = new CompoundTag();
                              enderData.locations.get(backpack).toNBT(location);
                              locations.put(backpack.toString(), location);
                        }

                        data.put("locations", locations);
                        enderTag.put(uuid.toString(), data);
                  }
            }));
            tag.put("EnderData", enderTag);
      }

      public void fromNbt(CompoundTag tag, ServerLevel level) {
            if (tag.contains("EnderData")) {
                  CompoundTag entries = tag.getCompound("EnderData");
                  for (String key : entries.getAllKeys()) {
                        CompoundTag dataTags = entries.getCompound(key);
                        NonNullList<ItemStack> itemStacks = NonNullList.create();
                        BackpackInventory.readStackNbt(dataTags, itemStacks);
                        CompoundTag trim = dataTags.getCompound("Trim");
                        MutableComponent playerName = Component.Serializer.fromJson(dataTags.getString("player_name"));
                        UUID uuid = UUID.fromString(key);
                        EnderInventory enderData = new EnderInventory(uuid, level);
                        enderData.setItemStacks(itemStacks).setTrim(trim).setPlayerName(playerName);

                        CompoundTag locations = dataTags.getCompound("locations");
                        for (String backpack : locations.getAllKeys()) {
                              Location location = new Location(locations.getCompound(backpack));
                              enderData.locations.put(UUID.fromString(backpack), location);
                        }

                        MAP.put(uuid, enderData);
                  }
                  MAP.remove(null);
            }
      }

}
