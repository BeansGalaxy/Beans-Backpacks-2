package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.general.BackpackInventory;
import com.beansgalaxy.backpacks.general.Kind;
import com.beansgalaxy.backpacks.general.PlaySound;
import com.beansgalaxy.backpacks.screen.BackSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BackpackItem extends Item {
      public final static int DEFAULT_COLOR = 10511680;

      public BackpackItem() {
            super(new Item.Properties().stacksTo(1));
      }

      @Override
      public InteractionResult useOn(UseOnContext ctx) {
            Player player = ctx.getPlayer();
            Direction direction = ctx.getClickedFace();
            BlockPos blockPos = ctx.getClickedPos().relative(direction);
            ItemStack backpackStack = ctx.getItemInHand();

            int x = blockPos.getX();
            double y = blockPos.getY() + 2d / 16;
            int z = blockPos.getZ();

            AABB box = BackpackEntity.newBox(blockPos, y, 10 / 16d, direction);
            boolean spaceEmpty = player.level().noCollision(box);
            if (spaceEmpty && doesPlace(player, x, y, z, direction, backpackStack, false)) {
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static InteractionResult hotkeyOnBlock(Player player, Direction direction, BlockPos blockPos) {
            ItemStack backpackStack = BackSlot.get(player).getItem();

            int x = blockPos.getX();
            double y = blockPos.getY() + 2d / 16;
            int z = blockPos.getZ();

            AABB box = BackpackEntity.newBox(blockPos, y, 10 / 16d, direction);
            if (player.level().noCollision(box) && BackpackItem.doesPlace(player, x, y, z, direction, backpackStack, true)) {
//                  if (player instanceof ServerPlayer serverPlayer) TODO: IMPLEMENT NETWORKING
//                        Services.NETWORK.SyncBackSlot(serverPlayer);
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
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
//                  if (player instanceof ServerPlayer serverPlayer) TODO: IMPLEMENT NETWORKING
//                        Services.NETWORK.SyncBackSlot(serverPlayer);
                  return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
      }

      public static boolean doesPlace(Player player, int x, double y, int z, Direction direction, ItemStack backpackStack, boolean fromBackSlot) {
            Level world = player.level();
            BlockPos blockPos = BlockPos.containing(x, y, z);

            NonNullList<ItemStack> stacks = fromBackSlot ?
                        BackSlot.getInventory(player).getItemStacks() : NonNullList.create();

            BackpackEntity backpackEntity = new BackpackEntity(player, world, x, y, z, direction,
                        backpackStack, stacks, rotFromBlock(blockPos, player) + 90);

            PlaySound.PLACE.at(backpackEntity);

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
            return Component.translatable(stack.getOrCreateTagElement("display").getString("name"));
      }


      public static BackpackInventory.Data getData(ItemStack stack) {
            CompoundTag display = stack.getOrCreateTagElement("display");

            String key = display.getString("key");
            String name = display.getString("name");
            Kind kind = Kind.fromStack(stack);
            int maxStacks = display.getInt("max_stacks");
            int color = stack.getItem() instanceof DyableBackpack dyableBackpack ? dyableBackpack.getColor(stack) : 0xFFFFFF;
            CompoundTag trim = stack.getTagElement("Trim");

            BackpackInventory.Data data = new BackpackInventory.Data(key, name, kind, maxStacks, color, trim);
            return data;
      }

      @Override
      public void verifyTagAfterLoad(CompoundTag tag) {
            CompoundTag display = tag.getCompound("display");
            String key = display.getString("key");
            String name = display.getString("name");
      }

}
