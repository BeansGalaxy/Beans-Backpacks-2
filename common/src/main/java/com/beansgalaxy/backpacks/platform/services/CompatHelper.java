package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface CompatHelper {
      String CURIOS = "curios";
      String TRINKETS = "trinkets";
      String MOD_MENU = "modmenu";
      String CLOTH_CONFIG = "cloth-config";

      boolean isModLoaded(String namespace);

      default boolean anyModsLoaded(String... namespaces) {
            for (String namespace: namespaces)
                  if (isModLoaded(namespace))
                        return true;
            return false;
      }

      default boolean allModsLoaded(String... namespaces) {
            for (String namespace: namespaces)
                  if (!isModLoaded(namespace))
                        return false;
            return true;
      }

      boolean backSlotDisabled(Player owner);

      CauldronInventory.FluidAttributes getFluidTexture(Fluid fluid, TextureAtlas blocksAtlas);

      default boolean graveModLoaded() {
            return anyModsLoaded("universal-graves", "yigd", "gravestones");
      }

      void setBackSlotItem(BackData data, ItemStack stack);

      ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem);

      boolean invokeListenersOnDeath(BackData backData);

      /* =============== METHODS BELOW ARE IN A PERMANENT LOCATION SAFE TO CALL FOR COMPATIBILITY ================ */

      /**
       * Updates an Ender Entry. If fields "name" or "trim" is Optional.empty(), it will not update those fields. <br>
       * @param uuid Entry to look up the specific ender entry.
       * @param name The displayed name of a bound ender backpack.
       * @param trim Taken directly from a trimmed ItemStack. The tag must contain a "pattern" & "material" string tag.
       *             If you are able to view a trimmed ItemStack, the tag is the contents of "Trim" and not "Trim" itself.
       **/
      default void updateEnderEntry(@NotNull UUID uuid, Optional<Component> name, Optional<CompoundTag> trim) {
            EnderStorage.Data data = EnderStorage.get().MAPPED_DATA.computeIfAbsent(uuid, uuid1 -> new EnderStorage.Data());
            name.ifPresent(data::setPlayerName);
            trim.ifPresent(data::setTrim);
      }

      /**
       * Updates an Ender Entry. If fields "name" or "trim" is null, it will not update those fields. <br>
       * @param uuid Entry to look up the specific ender entry.
       * @param name Written as a snbt string. ( §9Like This! )
       * @param translatable If true, the name field will be treated as a translation.
       * @param trim Written as a snbt tag. ( {pattern:"", material:""} )
       **/
      default void updateEnderEntry(@NotNull UUID uuid, @Nullable String name, boolean translatable, @Nullable String trim) {
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

      /**
       * Creates a bound Ender Backpack to the provided UUID, if empty it will default to the first player who interacts with it <br>
       * @param uuid Owner of the backpack inventory.
       * @param persistent If true, the owner tag cannot be overridden unless empty.
       **/
      default ItemStack createEnderBackpack(Optional<UUID> uuid, boolean persistent) {
            ItemStack ePack = Services.REGISTRY.getEnder().getDefaultInstance();
            CompoundTag tag = ePack.getOrCreateTag();

            uuid.ifPresent(in -> tag.putUUID("owner", in));

            if (persistent)
                  tag.putBoolean("persistent_ender", true);

            return ePack;
      }

      /**
       * This method places a new backpack dependent on the ItemStack. If a backpack cannot be created from the ItemStack null will be returned <br>
       * @param onDeath Determines whether to unbind an Ender Backpack if config allows
       * @param direction Direction.UP & Direction.DOWN implies the backpack is centered in the block, otherwise it will be hung and yaw will be ignored
       * @param uuid uuid of the player it is placed by or in the case of Ender Backpacks, the owner of the inventory
       * @param stacks ItemStacks of the backpack. Can be null or empty. Will be ignored if Ender Backpack
       * @return The Backpack entity , else <code>null</code> if ItemStack cant turn into entity.
       * Cast to {@link EntityAbstract EntityAbstract} at the risk of refactoring breaking compat.
       **/

      @Nullable
      static Entity createBackpackEntity(ItemStack backpackStack, int x, double y, int z, float yaw, boolean onDeath,
                                         Direction direction, Level level, UUID uuid, NonNullList<ItemStack> stacks) {
            return EntityAbstract.create(backpackStack, x, y, z, yaw, onDeath, direction, level, uuid, stacks);
      }

      /**
       * Grabs the literal non-copy back inventory whether they have a backpack equipped or not.
       * <p> Be aware the tooltip graphic might need you to update the player's client after making changes.
       * Use `updateBackpackInventory2C` below to sync the inventory.
       **/
      static NonNullList<ItemStack> getBackpackInventory(Player player) {
            return BackData.get(player).backpackInventory.getItemStacks();
      }

      /**
       * Syncs the ServerPlayer's back inventory to that player's client.
       **/
      static void updateBackpackInventory2C(ServerPlayer player) {
            Services.NETWORK.backpackInventory2C(player);
      }

      /**
       * Collects the ItemStack worn by either the BackSlot, Trinkets, or Curios.
       * <p>Will cause Curios/Trinkets instabilities if this stack is overridden. Always use `setBackStack()` below especially if making changes other than NBT or count.
       **/
      static ItemStack getBackStack(Player player) {
            return BackData.get(player).getStack();
      }

      /**
       * Sets the BackData stack and updates any other variables or compatibilities. ItemStack.EMPTY
       * @param backStack Passing ItemStack.EMPTY does not clear the back inventory but works otherwise.
       **/
      static void setBackStack(Player player, ItemStack backStack) {
            BackData.get(player).set(backStack);
      }

      /**
       * This hold all the data for the player. Powerful but refactors may break any compat with this method. Use if no other alternatives.
       **/
      static BackData getBackData(Player player) {
            return BackData.get(player);
      }
}
