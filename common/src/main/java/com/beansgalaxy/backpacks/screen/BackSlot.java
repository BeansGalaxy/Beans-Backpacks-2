package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.general.PlaySound;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
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
      public static int SLOT_INDEX;
      private final Player owner;
      public boolean sprintKeyIsPressed = false;

      public final BackpackInventory backpackInventory = new BackpackInventory() {

            NonNullList<Player> playersViewing = NonNullList.create();

            @Override
            public NonNullList<Player> getPlayersViewing() {
                  return playersViewing;
            }

            private final NonNullList<ItemStack> itemStacks = NonNullList.create();

            @Override
            public NonNullList<ItemStack> getItemStacks() {
                  return this.itemStacks;
            }

            @Override
            public void playSound(PlaySound sound) {
                  sound.at(owner);
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

//          TODO: IMPLEMENT BACKPACK

//            if (yawMatches && looking) { // INTERACT WITH BACKPACK CODE GOES HERE
//                  Backpack backpack = new Backpack(viewer.level()) {
//                        public Entity getOwner() {
//                              return owner;
//                        }
//
//                        public void setChanged() {
//                              if (owner instanceof ServerPlayer serverPlayer)
//                                    Services.NETWORK.SyncBackpackInventory(serverPlayer);
//                        }
//
//                        public void stopOpen(Player player) {
//                              BackSlot backSlot = get(owner);
//                              backSlot.removeViewer(viewer);
//                              if (backSlot.getViewers() < 1)
//                                    PlaySound.CLOSE.at(owner);
//                        }
//
//                        public void updateViewers() {
//                        }
//
//                        public void playSound(PlaySound sound) {
//                              sound.at(owner, 0.3f);
//                        }
//                  };
//                  NonNullList<ItemStack> itemStacks = getInventory(owner).getItemStacks();
//                  backpack.initDisplay(backpackStack);
//                  backpack.itemStacks = itemStacks;
//                  if (viewer.level() instanceof ServerLevel serverLevel)
//                        serverLevel.addWithUUID(backpack);
//                  viewer.openMenu(backpack);
//
//                  // ENABLE THIS LINE OF CODE BELOW TO SHOW WHEN THE BACKPACK IS INTERACTED WITH
//                  //owner.level().addParticle(ParticleTypes.FIREWORK, newX, viewer.getEyeY() + 0.1, newZ, 0, 0, 0);
//
//                  PlaySound.OPEN.at(owner);
//
//                  if (!viewer.level().isClientSide() && viewer.containerMenu != viewer.inventoryMenu) {
//                        backSlot.addViewer(viewer);
//                  }
//
//                  return InteractionResult.sidedSuccess(!viewer.level().isClientSide);
//            }

            return InteractionResult.PASS;
      }

      public static List<ResourceLocation> getTextures() {
            AdvancementTree manager = Minecraft.getInstance().getConnection().getAdvancements().getTree();
            boolean hasEndGoal = manager.get(ResourceLocation.tryParse("end/root")) != null;
            if (hasEndGoal)
                  return List.of(SLOT_ELYTRA, SLOT_BACKPACK);

            return List.of(SLOT_BACKPACK);
      }

      public int getMaxStackSize() {
            return 1;
      }

      public boolean mayPlace(ItemStack stack) {
            return Kind.isWearable(stack);
      }

      public boolean mayPickup(Player playerEntity) {
            ItemStack itemStack = this.getItem();
            return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && (getInventory(playerEntity).getItemStacks().isEmpty() || Kind.ELYTRA.is(itemStack));
      }

      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return null;
      }

      public boolean isActive() {
            return !owner.isCreative();
      }

      public void setChanged() {
            ItemStack stack = this.getItem();
            if (stack.isEmpty())
                  backpackInventory.clearViewers();
            else if (Kind.isBackpack(stack)) {
                  CompoundTag display = stack.getOrCreateTagElement("display");
                  String key = display.getString("key");
                  String name = display.getString("name");
                  Kind kind = Kind.fromStack(stack);
                  int maxStacks = display.getInt("max_stacks");
                  this.backpackInventory.data.update(key, name, kind, maxStacks);
            }
//            if (owner instanceof ServerPlayer serverPlayer) { TODO: IMPLEMENT NETWORK
//                  Services.NETWORK.SyncBackSlot(serverPlayer);
//                  Services.NETWORK.SyncBackpackInventory(serverPlayer);
//            }
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


            if (slotIndex == SLOT_INDEX + 1 && Kind.POT.is(backStack)) {
                  if (actionType == ClickType.THROW && cursorStack.isEmpty()) {
                        int count = button == 0 ? 1 : Math.min(stack.getCount(), maxStack);
                        ItemStack itemStack = backpackInventory.removeItem(0, count);
                        player.drop(itemStack, true);
                        return false;
                  }
                  if (actionType == ClickType.SWAP) {
                        ItemStack itemStack = playerInventory.getItem(button);
                        if (itemStack.isEmpty()) {
                              if (backpackStack.getCount() > maxStack) {
                                    playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                    backpackStack.shrink(maxStack);
                                    return false;
                              }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                        }
                        else {
                              if (backpackStack.isEmpty())
                                    return true;
                              if (backpackStack.getCount() > maxStack)
                                    if (playerInventory.add(-1, itemStack)) {
                                          playerInventory.setItem(button, backpackStack.copyWithCount(maxStack));
                                          backpackStack.shrink(maxStack);
                                          return false;
                                    }
                              playerInventory.setItem(button, backpackInventory.removeItemNoUpdate(0));
                              backpackInventory.insertItem(itemStack, itemStack.getCount());
                        }
                        return false;
                  }
                  if (button == 1 && cursorStack.isEmpty() && backpackStack.getCount() > maxStack) {
                        int count = Math.max(1, maxStack / 2);
                        ItemStack splitStack = backpackInventory.removeItem(0, count);
                        inventoryMenu.setCarried(splitStack);
                        return false;
                  }
            }

            if (actionType == ClickType.THROW)
                  return true;

            if (slotIndex < SLOT_INDEX && backSlot.sprintKeyIsPressed && backStack.isEmpty() && Kind.isWearable(stack)) {
                  backSlot.safeInsert(stack);
                  return false;
            }

            if (actionType == ClickType.QUICK_MOVE) {
                  if (slotIndex == SLOT_INDEX + 1) {
                        if (Kind.POT.is(backStack))
                              playerInventory.add(-1, backpackInventory.removeItemNoUpdate(0));
                        else if (!Kind.isBackpack(backStack) && Kind.isWearable(backStack)) {
                              player.getInventory().add(-1, backpackStack);
                              if (backpackStack.isEmpty())
                                    backpackInventory.removeItemNoUpdate(0);
                        } else
                              inventoryMenu.quickMoveStack(player, slotIndex);
                        return false;
                  }
            }
            else if (Kind.isWearable(backStack)) {
                  if (backSlot.sprintKeyIsPressed) {
                        if (slotIndex == SLOT_INDEX && Kind.isStorage(stack)) {
                              if (Kind.POT.is(stack))
                                    return false;
                              if (backpackInventory.isEmpty())
                                    player.getInventory().add(-1, stack);
                              else {
//                                    Backpack.drop(player, stack, backpackInventory.getItemStacks()); TODO: IMPLEMENT BACKPACK
                                    stack.setCount(0);
                              }
                              return false;
                        }
                        if (slotIndex == SLOT_INDEX + 1) {
                              Item compareItem = backpackInventory.getItem(0).copy().getItem();
                              boolean continueInsert = true;
                              boolean itemRemoved = false;
                              while (!backpackInventory.isEmpty() && backpackInventory.getItem(0).is(compareItem) && continueInsert) {
                                    continueInsert = playerInventory.add(-1, backpackInventory.removeItemSilent(0));
                                    itemRemoved = true;
                              }
                              if (itemRemoved)
                                    PlaySound.TAKE.toClient(player);
                              return false;
                        }
                        if (slotIndex < SLOT_INDEX) {
                              if (actionType == ClickType.PICKUP_ALL)
                                    moveAll(backpackInventory, inventoryMenu);
                              else slot.set(backpackInventory.insertItem(stack, stack.getCount()));
                        } else {
                              playerInventory.add(-1, stack);
                        }
                        return false;
                  }
                  else {
                        if (slotIndex == SLOT_INDEX + 1) {
                              if (actionType == ClickType.PICKUP_ALL)
                                    return false;

                              if (button == 1) {
                                    if (!cursorStack.is(backpackStack.getItem()) && !cursorStack.isEmpty()) {
                                          backpackInventory.insertItem(cursorStack, 1);
                                          return false;
                                    }
                              }
                              if (button == 0) {
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

      public boolean pickupItemEntity(Inventory instance, ItemStack stack) {
            if (stack.isEmpty())
                  return false;

            Player player = instance.player;
            BackSlot backSlot = BackSlot.get(player);
            BackpackInventory backpackInventory = BackSlot.getInventory(player);

            if (backpackInventory.data.kind == null || !Kind.isStorage(backSlot.getItem()))
                  return instance.add(-1, stack);

            if (backpackInventory.canPlaceItem(stack)) {
                  instance.items.forEach(stacks -> {
                        if (stacks.is(stack.getItem())) {
                              int present = stacks.getCount();
                              int inserted = stack.getCount();
                              int count = present + inserted;
                              int remainder = Math.max(0, count - stack.getMaxStackSize());
                              count -= remainder;

                              stacks.setCount(count);
                              stack.setCount(remainder);
                        }
                  });

                  backpackInventory.getItemStacks().forEach(stacks -> {
                        if (stacks.is(stack.getItem())) {
                              backpackInventory.insertItemSilent(stack, stack.getCount());
                              backpackInventory.setChanged();
                        }
                  });
            }

            if (stack.isEmpty())
                  return true;

            if (instance.add(-1, stack))
                  return true;

            if (backpackInventory.canPlaceItem(stack))
                  return backpackInventory.insertItemSilent(stack, stack.getCount()).isEmpty();

            return false;
      }
}
