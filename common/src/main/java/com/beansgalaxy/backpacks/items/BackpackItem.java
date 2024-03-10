package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.access.BucketAccess;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.screen.BackpackInventory;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.EntityAbstract;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.screen.CauldronInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class BackpackItem extends Item {
      public BackpackItem(Item.Properties properties) {
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
            if (kind == Kind.CAULDRON) {
                  return handleCauldronClick(backStack, player, access, cursorStack, level);
            }

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

      private static boolean handleCauldronClick(ItemStack backStack, Player player, SlotAccess access, ItemStack cursorStack, Level level) {
            if (cursorStack.getItem() instanceof BucketAccess bucketAccess) {
                  Fluid insertedFluid = bucketAccess.beans_Backpacks_2$getFluid();
                  Fluid fluid = CauldronInventory.interact(backStack, insertedFluid);
                  if (fluid == null)
                        return backStack.hasTag() && backStack.getTagElement("fluid") != null;

                  if (fluid.isSame(Fluids.EMPTY)) {
                        if (level.isClientSide())
                              Tooltip.playSound(insertedFluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY, 1, 0.4f);
                        access.set(Items.BUCKET.getDefaultInstance());
                        return true;
                  } else {
                        ItemStack bucket = fluid.getBucket().getDefaultInstance();
                        if (level.isClientSide())
                              fluid.getPickupSound().ifPresent(soundEvent -> Tooltip.playSound(soundEvent, 1, 0.4f));

                        if (cursorStack.getCount() == 1)
                              access.set(bucket);
                        else {
                              cursorStack.shrink(1);
                              player.getInventory().placeItemBackInInventory(bucket);
                        }

                        return true;
                  }
            } else
                  return backStack.hasTag() && backStack.getTagElement("fluid") != null;
      }

      public static void handleQuickMove(Inventory playerInventory, BackpackInventory backpackInventory) {
            ItemStack stack = backpackInventory.getItem(0);
            if (stack.isEmpty() || Kind.CAULDRON.is(backpackInventory.getLocalData().kind()))
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

            boolean inserted = false;
            boolean canceled = false;
            while (!canceled)
            {
                  int i = playerInventory.getSlotWithRemainingSpace(stack);
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
                  backpackInventory.insertItemSilent(backpackStack, backpackStack.getCount());

            if (!backpackInventory.isEmpty() && backpackInventory.getItem(0).isEmpty())
                  backpackInventory.removeItemNoUpdate(0);
      }

      public static InteractionResult hotkeyOnBlock(Player player, Direction direction, BlockPos clickedPos) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();

            if (useOnBlock(player, direction, clickedPos, backStack, true)) {
                  backData.setChanged();
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

            int invert = player.isCrouching() ? -1 : 1;
            int x = Mth.floor(pos.x);
            double y = pos.y + ((direction.getAxis().isHorizontal() ? 11 : 10) / 16d) * invert;
            int z = Mth.floor(pos.z);

            AABB box = entityAbstract.getBoundingBox().move(0, 10d / 16 * invert, 0);
            boolean spaceEmpty = player.level().noCollision(box);
            if (spaceEmpty && doesPlace(player, x, y, z, direction, backpackStack, fromBackSlot)) {
                  BackData.get(player).setChanged();
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static boolean doesPlace(Player player, int x, double y, int z, Direction direction, ItemStack backpackStack, boolean fromBackSlot) {
            Traits.LocalData traits = Traits.LocalData.fromstack(backpackStack);
            if (traits == null || traits.key.isEmpty())
                  return false;

            NonNullList<ItemStack> stacks = fromBackSlot ?
                        BackData.get(player).backpackInventory.getItemStacks() : NonNullList.create();

            BlockPos blockPos = BlockPos.containing(x, y, z);
            float yaw = rotFromBlock(blockPos, player) + 90;

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
            String key = stack.getOrCreateTagElement("display").getString("key");
            return Component.translatableWithFallback("tooltip.beansbackpacks.name." + key , Traits.get(key).name);
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

      public static ItemStack stackFromKey(String key) {
            Traits traits = Traits.get(key);
            CompoundTag display = new CompoundTag();
            display.putString("key", key);

            ItemStack stack = traits.kind.getItem().getDefaultInstance();
            stack.getOrCreateTag().put("display", display);

            return stack;
      }
}
