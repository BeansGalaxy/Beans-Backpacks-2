package com.beansgalaxy.backpacks.platform.services;

import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
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

      default boolean graveModLoaded() {
            return anyModsLoaded(new String[]{"universal-graves", "yigd", "gravestones"});
      }

      void setBackSlotItem(BackData data, ItemStack stack);

      ItemStack getBackSlotItem(BackData backData, ItemStack defaultItem);

      boolean backSlotDisabled(Player owner);

      /* =============== METHODS BELOW ARE IN A PERMANENT LOCATION SAFE TO CALL FOR COMPATIBILITY ================ */

      /**
       * This collects what it can from a player's BackData and drops it as a backpack <br>
       * Any items that are not safely inside a backpack it will be returned as a NonNullList ae. equipped elytras <br>
       * @param direction Direction.UP / Direction.DOWN implies the backpack is centered in the block, otherwise it will be hung and yaw will be ignored
       * @return Any items NOT safely inside a backpack ae. equipped elytras or items stored in a pot
       **/
      static NonNullList<ItemStack> dropAndCollectBackData(Player player, int x, double y, int z, Direction direction, float yaw) {
            return BackData.get(player).drop(x, y, z, direction, yaw);
      }

      /**
       * This method places a new backpack dependent on the ItemStack. If a backpack cannot be created from the ItemStack null will be returned <br>
       * @param onDeath Determines whether to unbind an Ender Backpack if config allows
       * @param direction Direction.UP / Direction.DOWN implies the backpack is centered in the block, otherwise it will be hung and yaw will be ignored
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
