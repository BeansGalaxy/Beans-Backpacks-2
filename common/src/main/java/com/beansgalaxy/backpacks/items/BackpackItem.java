package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BackpackItem extends Item {
      public BackpackItem() {
            super(new Item.Properties().stacksTo(1));
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

      public static boolean stackedOnMe(ItemStack thisStack, ItemStack thatStack, ClickAction clickAction, Player player, SlotAccess access) {
            Kind kind = Kind.fromStack(thisStack);
            if (kind == null)
                  return false;

            BackData backData = BackData.get(player);
            BackpackInventory backpackInventory = backData.backpackInventory;

            if (thisStack != backData.getStack())
                  return false;

            if (backpackInventory.isEmpty())
                  if (thatStack.isEmpty() || Kind.isWearable(thatStack))
                        return false;

            ItemStack backpackStack = backpackInventory.getItem(0);
            if (clickAction == ClickAction.SECONDARY) {
                  if (thatStack.isEmpty()) {
                        int count = Math.max(1, backpackStack.getCount() / 2);
                        access.set(backpackInventory.removeItem(0, count));
                  }
                  else {
                        backpackInventory.insertItem(thatStack, 1);
                  }
                  return true;
            }

            Inventory playerInventory = player.getInventory();
            if (backData.actionKeyPressed && thatStack.isEmpty() && playerInventory.getFreeSlot() != -1) {
                  if (Kind.POT.is(thisStack))
                        playerInventory.add(-2, backpackInventory.removeItemNoUpdate(0));
                  else {
                        playerInventory.add(-2, backpackStack);
                        if (backpackStack.isEmpty())
                              backpackInventory.removeItemNoUpdate(0);
                  }
                  return true;
            }

            return access.set(backpackInventory.returnItem(0, thatStack));
      }

      public static InteractionResult hotkeyOnBlock(Player player, Direction direction, BlockPos clickedPos) {
            BackData backData = BackData.get(player);
            ItemStack backpackStack = backData.getStack();

            if (useOnBlock(player, direction, clickedPos, backpackStack, true)) {
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
            AABB box = BackpackEntity.newBox(blockPos, y, 9 / 16d, direction);
            double yOffset = 2d / 16;

            if (isVertical) {
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

      public static InteractionResult useOnBackpack(Player player, BackpackEntity backpackEntity, ItemStack backpackStack, boolean fromBackSlot) {
            Vec3 pos = backpackEntity.position();
            Direction direction = backpackEntity.direction;

            int invert = player.isCrouching() ? -1 : 1;
            int x = Mth.floor(pos.x);
            double y = pos.y + 11d / 16 * invert;
            int z = Mth.floor(pos.z);

            AABB box = backpackEntity.getBoundingBox().move(0, 10d / 16 * invert, 0);
            boolean spaceEmpty = player.level().noCollision(box);
            if (spaceEmpty && doesPlace(player, x, y, z, direction, backpackStack, fromBackSlot)) {
                  BackData.get(player).setChanged();
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static boolean doesPlace(Player player, int x, double y, int z, Direction direction, ItemStack backpackStack, boolean fromBackSlot) {
            Traits.LocalData traits = Traits.LocalData.fromStack(backpackStack);
            if (traits == null || traits.key.isEmpty())
                  return false;

            Level world = player.level();
            BlockPos blockPos = BlockPos.containing(x, y, z);

            NonNullList<ItemStack> stacks = fromBackSlot ?
                        BackData.get(player).backpackInventory.getItemStacks() : NonNullList.create();

            BackpackEntity backpackEntity = new BackpackEntity(player, world, x, y, z, direction,
                        traits, stacks, rotFromBlock(blockPos, player) + 90);

            PlaySound.PLACE.at(backpackEntity, traits.kind());
            if (player instanceof ServerPlayer serverPlayer)
                  Services.REGISTRY.triggerPlace(serverPlayer, traits.key);

            backpackStack.shrink(1);
            return true;
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
            return Tooltip.name(stack);
      }

      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level $$1, List<Component> components, TooltipFlag flag) {
            Tooltip.lore(stack, components);
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
