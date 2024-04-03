package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface CompatHelper {
      String CURIOS = "curios";
      String TRINKETS = "trinkets";

      boolean isModLoaded(String namespace);

      default boolean anyModsLoaded(String[] namespaces) {
            for (String namespace: namespaces)
                  if (isModLoaded(namespace))
                        return true;
            return false;
      }

      boolean backSlotDisabled(Player owner);

      CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas);

      default boolean graveModLoaded() {
            return anyModsLoaded(new String[]{"universal-graves", "yigd", "gravestones"});
      }

      static NonNullList<ItemStack> onDeathForGraveMods(Player player, int x, double y, int z, Direction direction, float yaw) {
            return BackData.get(player).drop(x, y, z, direction, yaw);
      }

      void setBackSlotItem(BackData data, ItemStack stack);

      ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem);

      default void updateEnderEntry(@NotNull UUID uuid, Optional<Component> name, Optional<CompoundTag> trim) {
            EnderStorage.Data data = EnderStorage.get().MAPPED_DATA.computeIfAbsent(uuid, uuid1 -> new EnderStorage.Data());
            name.ifPresent(data::setPlayerName);
            trim.ifPresent(data::setTrim);
      }

      default void updateEnderEntry(@NotNull UUID uuid, String name, boolean translatable, String trim) {
            Optional<Component> component;
            if (name == null)
                  component = Optional.empty();
            else if (translatable)
                  component = Optional.of(Component.translatable(name));
            else
                  component = Optional.of(Component.literal(name));

            Optional<CompoundTag> tag = Optional.empty();
            if (trim != null && Constants.isEmpty(trim)) {
                  try {
                        tag = Optional.of(NbtUtils.snbtToStructure(trim));
                  } catch (CommandSyntaxException e) {
                        Constants.LOG.error(Constants.MOD_ID + ": Failed to create Ender Data with the name \"" + name + "\"");
                  }
            }

            updateEnderEntry(uuid, component, tag);
      }

      default ItemStack createEnderBackpack(Optional<UUID> uuid, boolean persistent) {
            ItemStack ePack = Services.REGISTRY.getEnder().getDefaultInstance();
            CompoundTag tag = ePack.getOrCreateTag();

            uuid.ifPresent(in -> tag.putUUID("owner", in));

            if (persistent)
                  tag.putBoolean("persistent_ender", true);

            return ePack;
      }
}
