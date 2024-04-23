package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.access.BucketLikeAccess;
import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.inventory.CauldronInventory;
import com.beansgalaxy.backpacks.inventory.PotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;
import java.util.Optional;

public class BackpackItem extends Item {

      public BackpackItem(Properties properties) {
            super(properties);
      }

      @Override
      public InteractionResult useOn(UseOnContext ctx) {
            Player player = ctx.getPlayer();
            Direction direction = ctx.getClickedFace();
            BlockPos clickedPos = ctx.getClickedPos();
            ItemStack backpackStack = ctx.getItemInHand();

            if (useOnBlock(player, direction, clickedPos, backpackStack, false)) {
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static boolean interact(ItemStack backStack, ClickAction clickAction, Player player, SlotAccess access, boolean shiftIsDown) {
            ItemStack cursorStack = access.get();
            Kind kind = Kind.fromStack(backStack);
            if (kind == null)
                  return false;

            BackData backData = BackData.get(player);
            BackpackInventory backpackInventory = backData.backpackInventory;
            if (backStack != backData.getStack())
                  return false;

            Level level = player.level();
            boolean quickMove = backData.actionKeyPressed || shiftIsDown;
            if (Kind.CAULDRON.is(kind))
                  return handleCauldronClick(backStack, player, access, cursorStack, level);
            if (Kind.POT.is(kind))
                  return handlePotClick(backStack, player, access, cursorStack, quickMove, clickAction);

            if (backpackInventory.isEmpty())
                  if (cursorStack.isEmpty() || Kind.isWearable(cursorStack))
                        return false;

            if (Kind.ENDER.is(kind) && level.isClientSide())
                  return true;

            if (quickMove && clickAction != ClickAction.SECONDARY) {
                  handleQuickMove(player.getInventory(), backpackInventory);
                  return true;
            }

            return access.set(BackpackMenu.menuInsert(clickAction == ClickAction.SECONDARY ? 1 : 0,
                        cursorStack, 0, backpackInventory));
      }

      private static boolean handlePotClick(ItemStack pot, Player player, SlotAccess access, ItemStack cursorStack, boolean quickMove, ClickAction clickAction) {
            Level level = player.level();
            if (quickMove && clickAction != ClickAction.SECONDARY) {
                  Inventory inventory = player.getInventory();
                  if (inventory.getFreeSlot() != -1) {
                        ItemStack take = PotInventory.take(pot, false, level);
                        if (take != null) {
                              inventory.placeItemBackInInventory(take);
                              return true;
                        }
                  }
                  return pot.getTagElement("back_slot") != null;
            }
            ItemStack tookStack = null;
            if (clickAction.equals(ClickAction.SECONDARY) && !cursorStack.isEmpty())
            {
                  ItemStack insertedStack = cursorStack.copyWithCount(1);
                  ItemStack add = PotInventory.add(pot, insertedStack, player);
                  if (add != null && add.isEmpty()) {
                        cursorStack.shrink(1);
                        return true;
                  }
            }
            else if (cursorStack.isEmpty())
                  tookStack = PotInventory.take(pot, ClickAction.SECONDARY.equals(clickAction), level);
            else
                  tookStack = PotInventory.add(pot, cursorStack, player);


            if (tookStack != null) {
                  access.set(tookStack);
                  return true;
            }

            return pot.getTagElement("back_slot") != null;
      }

      private static boolean handleCauldronClick(ItemStack backStack, Player player, SlotAccess access, ItemStack cursorStack, Level level) {
            Item bucket = cursorStack.getItem();

            Item returned = null;
            if (CauldronInventory.getBucket(backStack) instanceof BucketsAccess bucketsAccess && bucketsAccess.getEmptyInstance().equals(bucket))
            {
                  Item remove = CauldronInventory.remove(backStack);
                  if (!remove.equals(Items.AIR)) {
                        returned = remove;
                  } else
                        return backStack.hasTag() && backStack.getTagElement("back_slot") != null;
            }
            else if (CauldronInventory.sizeLeft(backStack) > 0)
                  returned = CauldronInventory.add(backStack, bucket);

            if (returned == null) return backStack.hasTag() && backStack.getTagElement("back_slot") != null;

            ItemStack newStack = ItemStack.EMPTY;
            Optional<SoundEvent> soundEvent = Optional.empty();
            if (bucket instanceof BlockItem blockItem && blockItem.getBlock() instanceof BucketLikeAccess bucketLikeAccess)
            {
                  if (returned.equals(bucketLikeAccess.getEmptyInstance())) {
                        newStack = bucketLikeAccess.getEmptyInstance().getDefaultInstance();
                        soundEvent = Optional.of(bucketLikeAccess.defaultPlaceSound());
                  } else {
                        newStack = bucketLikeAccess.getFilledInstance().getDefaultInstance();
                        soundEvent = bucketLikeAccess.getPickupSound();
                  }
            }
            else if (bucket instanceof BucketsAccess bucketsAccess && returned.equals(bucketsAccess.getEmptyInstance())) {
                  newStack = bucketsAccess.getEmptyInstance().getDefaultInstance();
                  soundEvent = Optional.of(bucketsAccess.defaultPlaceSound());
            }
            else if (returned instanceof BucketsAccess bucketsAccess) {
                  newStack = returned.getDefaultInstance();
                  soundEvent = bucketsAccess.getPickupSound();
            }

            if (level.isClientSide() && soundEvent.isPresent())
                  Tooltip.playSound(soundEvent.get(), 1, 0.4f);

            if (cursorStack.getCount() == 1)
                  access.set(newStack);
            else {
                  cursorStack.shrink(1);
                  player.getInventory().placeItemBackInInventory(newStack);
            }

            return true;
      }

      public static void handleQuickMove(Inventory playerInventory, BackpackInventory backpackInventory) {
            ItemStack stack = backpackInventory.getItem(0);
            if (stack.isEmpty() || Kind.CAULDRON.is(backpackInventory.getTraits().kind))
                  return;

            ItemStack backpackStack = backpackInventory.removeItemSilent(0);
            ItemStack stack1 = backpackInventory.getItem(1);
            int count = backpackStack.getCount();
            int maxStackSize = backpackStack.getMaxStackSize();
            if (maxStackSize > count && ItemStack.isSameItemSameTags(backpackStack, stack1)) {
                  int normalizedCount = count - maxStackSize;
                  stack1.shrink(normalizedCount);
                  backpackStack.grow(normalizedCount);
            }

            handleQuickMove(playerInventory, backpackInventory, backpackStack, 0);
      }

      public static void handleQuickMove(Inventory playerInventory, BackpackInventory backpackInventory, int index) {
            if (playerInventory.getFreeSlot() != -1) {
                  ItemStack backpackStack = backpackInventory.removeItemSilent(index);
                  handleQuickMove(playerInventory, backpackInventory, backpackStack, index);
            }
      }

      private static void handleQuickMove(Inventory playerInventory, BackpackInventory backpackInventory, ItemStack backpackStack, int index) {
            int maxStackSize = backpackStack.getMaxStackSize();
            boolean inserted = false;
            boolean canceled = false;
            while (!canceled)
            {
                  int i = playerInventory.getSlotWithRemainingSpace(backpackStack);
                  if (i == -1)
                        i = playerInventory.getFreeSlot();

                  if (i == -1)
                        canceled = true;
                  else {
                        ItemStack clickedStack = playerInventory.getItem(i);
                        if (ItemStack.isSameItemSameTags(backpackStack, clickedStack))
                        {
                              int count1 = backpackStack.getCount();
                              int total = count1 + clickedStack.getCount();
                              if (total < maxStackSize)
                              {
                                    clickedStack.grow(count1);
                                    backpackStack.shrink(count1);
                              }
                              else {
                                    clickedStack.setCount(maxStackSize);
                                    backpackStack.setCount(total - maxStackSize);
                              }
                        }
                        else {
                              if (backpackStack.getCount() > maxStackSize)
                              {
                                    playerInventory.setItem(i, backpackStack.copyWithCount(maxStackSize));
                                    backpackStack.shrink(maxStackSize);
                              }
                              else
                                    playerInventory.setItem(i, backpackStack.copyAndClear());
                        }

                        if (backpackStack.isEmpty())
                        {
                              canceled = true;
                              inserted = true;
                        }
                  }
            }

            if (inserted)
                  backpackInventory.playSound(PlaySound.TAKE);

            if (!backpackStack.isEmpty())
                  backpackInventory.insertItemSilent(backpackStack, backpackStack.getCount(), 0);

            if (backpackInventory.getContainerSize() > index && backpackInventory.getItem(index).isEmpty())
                  backpackInventory.removeItemNoUpdate(index);
      }


      public static InteractionResult hotkeyOnBlock(Player player, Direction direction, BlockPos clickedPos) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();

            if (useOnBlock(player, direction, clickedPos, backStack, true)) {
                  backData.setChanged();
                  if (player instanceof ServerPlayer serverPlayer)
                        SyncBackInventory.send(serverPlayer);
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      private static Boolean useOnBlock(Player player, Direction direction, BlockPos clickedPos, ItemStack backpackStack, boolean fromBackSlot) {
            Level level = player.level();
            BlockPos blockPos;

            boolean isVertical = direction.getAxis().isVertical();
            if (isVertical && level.getBlockState(clickedPos).getCollisionShape(level, clickedPos).isEmpty()) {
                  blockPos = clickedPos;
            } else
                  blockPos =  clickedPos.relative(direction);

            int y = blockPos.getY();
            AABB box = EntityAbstract.newBox(blockPos, y, 9 / 16d, direction);
            double yOffset = (direction.getAxis().isHorizontal() ? 3 : 1) / 16d;

            if (isVertical) { // PUSHES ENTITY DOWN TO THE NEAREST COLLISION IF VERTICAL
                  boolean isRelative = !Objects.equals(blockPos, clickedPos);
                  AABB $$4 = new AABB(blockPos);
                  if (isRelative)
                        $$4 = $$4.expandTowards(0.0, -1.0, 0.0);

                  Iterable<VoxelShape> $$5 = level.getCollisions(null, $$4);
                  yOffset += Shapes.collide(Direction.Axis.Y, box, $$5, isRelative ? -2.0 : -1.0);

                  if (level.noCollision($$4))
                        yOffset += 1;
            }

            boolean spaceEmpty = level.noCollision(box.move(0, yOffset, 0));
            return spaceEmpty && doesPlace(player, blockPos.getX(), y + yOffset, blockPos.getZ(), direction, backpackStack, fromBackSlot);
      }

      public static InteractionResult useOnBackpack(Player player, EntityAbstract entityAbstract, ItemStack backpackStack, boolean fromBackSlot) {
            Vec3 pos = entityAbstract.position();
            Direction direction = entityAbstract.direction;

            int x = Mth.floor(pos.x);
            double y = pos.y + ((direction.getAxis().isHorizontal() ? 11 : 10) / 16d);
            int z = Mth.floor(pos.z);

            AABB box = entityAbstract.getBoundingBox().move(0, 10d / 16, 0);
            boolean spaceEmpty = player.level().noCollision(box);
            if (spaceEmpty && doesPlace(player, x, y, z, direction, backpackStack, fromBackSlot)) {
                  BackData.get(player).setChanged();
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static boolean doesPlace(Player player, int x, double y, int z, Direction direction, ItemStack backpackStack, boolean fromBackSlot) {
            NonNullList<ItemStack> stacks = fromBackSlot ?
                        BackData.get(player).backpackInventory.getItemStacks() : NonNullList.create();

            BlockPos blockPos = BlockPos.containing(x, y, z);
            float yaw = rotFromBlock(blockPos, player) + 90;

            if (player.level().isClientSide())
                  return true;

            EntityAbstract entityAbstract =
                        EntityAbstract.create(backpackStack, x, y, z, yaw, false, direction, player, stacks);

            return entityAbstract != null;
      }

      private static float rotFromBlock(BlockPos blockPos, Player player) {
            Vec3 CPos = blockPos.getCenter();
            float YRot = (float) Math.toDegrees(Math.atan2
                        (CPos.z - player.getZ(), CPos.x - player.getX()));
            if (YRot < -180) YRot += 360;
            else if (YRot > 180) YRot -= 360;
            return YRot;
      }

      @Override
      public Component getName(ItemStack stack) {
            CompoundTag display = stack.getTag();
            Traits traits = Kind.getTraits(stack);
            if (display != null && display.contains("backpack_id")) {
                  String key = display.getString("backpack_id");
                  return Component.translatableWithFallback("tooltip.beansbackpacks.name." + key, traits.name);
            }
            return super.getName(stack);
      }

      @Override @Deprecated // Since 20.1-0.18-v2
      public void verifyTagAfterLoad(CompoundTag tag) {
            if (tag.contains("display")) {
                  CompoundTag display = tag.getCompound("display");
                  if (display.contains("key")) {
                        String key = display.getString("key");
                        display.remove("key");
                        if (display.isEmpty())
                              tag.remove("display");
                        switch (key) {
                              case "leather", "iron", "ender", "winged" -> {}
                              case "netherite" -> tag.putString("backpack_id", "null");
                              default -> tag.putString("backpack_id", key);
                        }
                  }
            }
            super.verifyTagAfterLoad(tag);
      }

      @Override
      public boolean isBarVisible(ItemStack stack) {
            return Tooltip.isBarVisible(stack);
      }

      @Override
      public int getBarWidth(ItemStack stack) {
            return Tooltip.getBarWidth(stack);
      }

      @Override
      public int getBarColor(ItemStack $$0) {
            return Tooltip.barColor;
      }
}
