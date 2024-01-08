package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.LocalData;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.platform.Services;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BackSlot extends Slot {
      private static final ResourceLocation SLOT_BACKPACK = new ResourceLocation("sprites/empty_slot_backpack");
      private static final ResourceLocation SLOT_ELYTRA = new ResourceLocation("sprites/empty_slot_elytra");
      public static final ResourceLocation BACKPACK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
      public final BackpackInventory.Viewable viewable = new BackpackInventory.Viewable();

      public static int SLOT_INDEX;
      private final Player owner;
      public boolean actionKeyPressed = false;

      public final BackpackInventory backpackInventory = new BackpackInventory() {

            @Override public Entity getOwner() {
                  return BackSlot.this.owner;
            }

            @Override public Viewable getViewable() {
                  return BackSlot.this.viewable;
            }

            NonNullList<Player> playersViewing = NonNullList.create();

            @Override public NonNullList<Player> getPlayersViewing() {
                  return playersViewing;
            }

            private final NonNullList<ItemStack> itemStacks = NonNullList.create();

            @Override public NonNullList<ItemStack> getItemStacks() {
                  return this.itemStacks;
            }

            @Override public LocalData getLocalData() {
                  ItemStack stack = BackSlot.this.getItem();
                  return BackpackItem.getItemTraits(stack);
            }
      };

      public BackSlot(int index, int x, int y, Player player) {
            super(new SimpleContainer(1), index, x, y);
            this.owner = player;
      }

      public static BackpackInventory getInventory(Player player) {
            return BackSlot.get(player).backpackInventory;
      }

      public static BackSlot get(Player player) {
            return (BackSlot) player.inventoryMenu.slots.get(SLOT_INDEX);
      }

      public static InteractionResult openPlayerBackpackMenu(Player viewer, Player owner) {
            BackSlot backSlot = BackSlot.get(owner);
            ItemStack backpackStack = backSlot.getItem();
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

                  // ENABLE THIS LINE OF CODE BELOW TO SHOW WHEN THE BACKPACK IS INTERACTED WITH
                  //owner.level().addParticle(ParticleTypes.FIREWORK, newX, viewer.getEyeY() + 0.1, newZ, 0, 0, 0);

                  PlaySound.OPEN.at(owner);
                  return InteractionResult.sidedSuccess(!viewer.level().isClientSide);
            }

            return InteractionResult.PASS;
      }

      public static List<ResourceLocation> getTextures() {
            AdvancementTree manager = Minecraft.getInstance().getConnection().getAdvancements().getTree();
            boolean hasEndGoal = manager.get(ResourceLocation.tryParse("end/root")) != null;
            if (hasEndGoal)
                  return List.of(SLOT_ELYTRA, SLOT_BACKPACK);

            return List.of(SLOT_BACKPACK);
      }

      public void replaceWith(BackSlot backSlot) {
            ItemStack backStack = backSlot.getItem();
            this.container.setItem(getContainerSlot(), backStack);

            NonNullList<ItemStack> stacks = backSlot.backpackInventory.getItemStacks();
            this.backpackInventory.getItemStacks().addAll(stacks);
      }

      @Override
      public int getMaxStackSize() {
            return 1;
      }

      @Override
      public boolean mayPlace(ItemStack stack) {
            return Kind.isWearable(stack);
      }

      @Override
      public boolean isActive() {
            ItemStack chestplateStack = owner.inventoryMenu.slots.get(6).getItem();
            boolean isDisabled = Constants.DISABLES_BACK_SLOT.contains(chestplateStack.getItem());
            return !owner.isCreative() && !isDisabled;
      }

      @Override
      public boolean mayPickup(Player playerEntity) {
            ItemStack itemStack = this.getItem();
            boolean backpackIsEmpty = getInventory(playerEntity).isEmpty();
            boolean standardCheck = itemStack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemStack);
            return standardCheck && backpackIsEmpty;
      }

      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return null;
      }

      @Override
      public void setChanged() {
            ItemStack stack = this.getItem();
            if (stack.isEmpty())
                  backpackInventory.clearViewers();
            if (owner instanceof ServerPlayer serverPlayer) {
                  Services.NETWORK.SyncBackSlot(serverPlayer);
            }
      }

      // RETURN FALSE TO CANCEL A PLAYER'S INVENTORY CLICK
      public static boolean continueSlotClick(int slotIndex, int button, ClickType actionType, Player player) {
            if (slotIndex < 0)
                  return true;

            InventoryMenu inventoryMenu = player.inventoryMenu;
            BackpackInventory backpackInventory = BackSlot.getInventory(player);
            Inventory playerInventory = player.getInventory();
            ItemStack cursorStack = inventoryMenu.getCarried();

            BackSlot backSlot = BackSlot.get(player);
            ItemStack backStack = backSlot.getItem();

            ItemStack backpackStack = backpackInventory.getItem(0);
            int maxStack = backpackStack.getMaxStackSize();

            Slot slot = inventoryMenu.slots.get(slotIndex);
            ItemStack stack = slot.getItem();


            if (slotIndex == SLOT_INDEX + 1 && Kind.POT.is(backStack))
            {
                  if (actionType == ClickType.THROW && cursorStack.isEmpty())
                  {
                        int count = button == 0 ? 1 : Math.min(stack.getCount(), maxStack);
                        ItemStack itemStack = backpackInventory.removeItem(0, count);
                        player.drop(itemStack, true);
                        return false;
                  }
                  if (actionType == ClickType.SWAP)
                  {
                        ItemStack itemStack = playerInventory.getItem(button);
                        if (itemStack.isEmpty()) {
                              if (backpackStack.getCount() > maxStack)
                              {
                                    playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                    backpackStack.shrink(maxStack);
                                    return false;
                              }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                        } else
                        {
                              if (backpackStack.isEmpty())
                                    return true;
                              if (backpackStack.getCount() > maxStack)
                                    if (playerInventory.add(-2, itemStack))
                                    {
                                          playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                          backpackStack.shrink(maxStack);
                                          return false;
                                    }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                              backpackInventory.insertItem(itemStack);
                        }
                        return false;
                  }
                  if (button == 1 && cursorStack.isEmpty() && backpackStack.getCount() > maxStack)
                  {
                        int count = Math.max(1, maxStack / 2);
                        ItemStack splitStack = backpackInventory.removeItem(0, count);
                        inventoryMenu.setCarried(splitStack);
                        return false;
                  }
            }

            if (actionType == ClickType.THROW)
                  return true;

            if (slotIndex < SLOT_INDEX && backSlot.actionKeyPressed && backStack.isEmpty() && backSlot.isActive() && Kind.isWearable(stack))
            {
                  backSlot.safeInsert(stack);
                  return false;
            }

            if (actionType == ClickType.QUICK_MOVE)
            {
                  if (slotIndex == SLOT_INDEX + 1)
                  {
                        if (Kind.POT.is(backStack))
                              playerInventory.add(-2, backpackInventory.removeItemNoUpdate(0));
                        else if (!Kind.isBackpack(backStack) && Kind.isWearable(backStack))
                        {
                              playerInventory.add(-2, backpackStack);
                              if (backpackStack.isEmpty())
                                    backpackInventory.removeItemNoUpdate(0);
                        } else
                              inventoryMenu.quickMoveStack(player, slotIndex);
                        return false;
                  }
            } else if (Kind.isWearable(backStack))
            {
                  if (backSlot.actionKeyPressed)
                  {
                        if (slotIndex == SLOT_INDEX && Kind.isStorage(stack))
                              if (backpackInventory.isEmpty() && !Kind.POT.is(stack))
                                    playerInventory.add(-2, stack);
                              else return false;

                        if (slotIndex == SLOT_INDEX + 1)
                        {
                              Item compareItem = backpackInventory.getItem(0).copy().getItem();
                              boolean continueInsert = true;
                              boolean itemRemoved = false;
                              while (!backpackInventory.isEmpty() && backpackInventory.getItem(0).is(compareItem) && continueInsert)
                              {
                                    continueInsert = playerInventory.add(-2, backpackInventory.removeItemSilent(0));
                                    itemRemoved = true;
                              }
                              if (itemRemoved)
                                    PlaySound.TAKE.at(player);
                              return false;
                        }
                        if (slotIndex < SLOT_INDEX) {
                              if (actionType == ClickType.PICKUP_ALL)
                                    moveAll(backpackInventory, inventoryMenu);
                              else slot.set(backpackInventory.insertItem(stack));
                        } else playerInventory.add(-2, stack);
                        return false;
                  } else
                  {
                        if (slotIndex == SLOT_INDEX + 1)
                        {
                              if (actionType == ClickType.PICKUP_ALL)
                                    return false;

                              if (button == 1 && !cursorStack.is(backpackStack.getItem()) && !cursorStack.isEmpty())
                              {
                                    backpackInventory.insertItem(cursorStack, 1);
                                    return false;
                              }
                              if (button == 0)
                              {
                                    inventoryMenu.setRemoteCarried(cursorStack);
                                    ItemStack returnStack = backpackInventory.returnItem(0, cursorStack);
                                    inventoryMenu.setCarried(returnStack);
                                    return false;
                              }
                        }
                  }
            }
            return true;
      }

      public void drop() {
            ItemStack backpackStack = this.getItem();
            NonNullList<ItemStack> itemStacks = backpackInventory.getItemStacks();
            Kind kind = Kind.fromStack(backpackStack);

            if (!Kind.isBackpack(backpackStack)) {
                  owner.spawnAtLocation(backpackStack.copy(), 0.5f);
                  if (Kind.POT.is(kind)) {
                        int iteration = 0;
                        int maxIterations = 72;
                        while (!itemStacks.isEmpty() && iteration < maxIterations) {
                              ItemStack stack = itemStacks.remove(iteration);
                              if (stack.getMaxStackSize() == 64) {
                                    owner.spawnAtLocation(stack, 0.5f);
                              } else while (stack.getCount() > 0) {
                                    int removedCount = Math.min(stack.getCount(), stack.getMaxStackSize());
                                    owner.spawnAtLocation(stack.copyWithCount(removedCount));
                                    stack.shrink(removedCount);
                              }
                              iteration++;
                        }
                        SoundEvent soundEvent = iteration >= maxIterations ? SoundEvents.DECORATED_POT_BREAK : SoundEvents.DECORATED_POT_SHATTER;
                        owner.playSound(soundEvent, 0.4f, 0.8f);
                  }
                  return;
            }

            BlockPos blockPos = owner.getOnPos();
            float yRot = owner.yBodyRot + 180;

            int x = blockPos.getX();
            double y = blockPos.getY() + 1.5;
            int z = blockPos.getZ();

            new BackpackEntity(owner, owner.level(), x, y, z, Direction.UP,
                        backpackStack, itemStacks, yRot);

            PlaySound.DROP.at(owner);
      }

      private static void moveAll(BackpackInventory backpackInventory, InventoryMenu inventoryMenu) {
            NonNullList<Slot> slots = inventoryMenu.slots;
            ItemStack cursorStack = inventoryMenu.getCarried();
            int matchingItemsTotalCount = 0;
            for (int j = 9; j < 45; j++) {
                  Slot thisSlot = slots.get(j);
                  ItemStack thisStack = thisSlot.getItem();
                  if (thisStack.is(cursorStack.getItem())) {
                        matchingItemsTotalCount += thisStack.getCount();
                        thisSlot.set(ItemStack.EMPTY);
                  }
            }
            if (matchingItemsTotalCount > 0)
                  backpackInventory.playSound(PlaySound.INSERT);
            while (matchingItemsTotalCount > 0) {
                  int itemsMaxCount = cursorStack.getMaxStackSize();
                  if (matchingItemsTotalCount > itemsMaxCount) {
                        backpackInventory.insertItemSilent(cursorStack.copy(), itemsMaxCount);
                        matchingItemsTotalCount -= itemsMaxCount;
                  } else {
                        backpackInventory.insertItemSilent(cursorStack.copy(), matchingItemsTotalCount);
                        matchingItemsTotalCount = 0;
                  }
            }
      }
}
