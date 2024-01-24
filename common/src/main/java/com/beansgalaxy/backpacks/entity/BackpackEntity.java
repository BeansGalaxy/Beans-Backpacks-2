package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BackpackEntity extends Backpack {
      private final UUID placedBy;
      public Direction direction;
      protected BlockPos pos;
      public double actualY;
      private static final int BREAK_TIMER = 25;
      public int wobble = 0;

      public BackpackEntity(EntityType<? extends Entity> type, Level level) {
            super(type, level);
            placedBy = null;
      }

      public BackpackEntity(Player player, Level world, int x, double y, int z, Direction direction,
                            Traits.LocalData traits, NonNullList<ItemStack> stacks, float yaw) {
            super(world);
            this.actualY = y;
            this.pos = BlockPos.containing(x, y, z);
            this.setDirection(direction);
            this.initDisplay(traits);
            this.placedBy = player.getUUID();

            if (!direction.getAxis().isHorizontal())
                  this.setYRot(yaw);

            if (!world.isClientSide()) {
                  world.gameEvent(player, GameEvent.ENTITY_PLACE, this.position());
                  world.addFreshEntity(this);
            }

            if (stacks != null && !stacks.isEmpty()) {
                  this.getInventory().getItemStacks().addAll(stacks);
                  stacks.clear();
            }
      }

      public static ItemStack toStack(BackpackEntity backpack) {
            Traits.LocalData localData = backpack.getLocalData();
            Item item = localData.kind().getItem();
            ItemStack stack = item.getDefaultInstance();
            String key = backpack.getKey();

            CompoundTag display = new CompoundTag();
            display.putString("key", key);
            stack.getOrCreateTag().put("display", display);

            CompoundTag trim = backpack.getTrim();
            if (!trim.isEmpty())
                  stack.addTagElement("Trim", trim);

            int color = backpack.getColor();
            if (color != DEFAULT_COLOR && stack.getItem() instanceof DyableBackpack)
                  stack.getOrCreateTagElement("display").putInt("color", color);

            if (localData.hoverName.toString().equals("empty"))
                  stack.resetHoverName();
            else
                  stack.setHoverName(localData.hoverName);

            return stack;
      }

      @Override
      protected float getEyeHeight(@NotNull Pose p_31784_, @NotNull EntityDimensions p_31785_) {
            return 6F / 16;
      }

      @Override @NotNull
      public Direction getDirection() {
            return this.direction;
      }

      protected void setDirection(Direction direction) {
            if (direction != null) {
                  this.direction = direction;
                  if (direction.getAxis().isHorizontal()) {
                        this.setNoGravity(true);
                        this.setYRot((float) direction.get2DDataValue() * 90);
                  }
                  this.xRotO = this.getXRot();
                  this.yRotO = this.getYRot();
                  this.recalculateBoundingBox();
            }
      }

      @Override
      public void setPos(double x, double y, double z) {
            this.actualY = y;
            this.pos = BlockPos.containing(x, y, z);
            this.recalculateBoundingBox();
            this.hasImpulse = true;
      }

      public static AABB newBox(BlockPos blockPos, double y, double height, Direction direction) {
            double x1 = blockPos.getX() + 0.5D;
            double z1 = blockPos.getZ() + 0.5D;
            double Wx = 8D / 32;
            double Wz = 8D / 32;
            if (direction != null) {
                  if (direction.getAxis().isHorizontal()) {
                        double D = 4D / 32;
                        double off = 6D / 16;
                        int stepX = direction.getStepX();
                        int stepZ = direction.getStepZ();
                        Wx -= D * Math.abs(stepX);
                        Wz -= D * Math.abs(stepZ);
                        x1 -= off * stepX;
                        z1 -= off * stepZ;
                  } else {
                        Wx -= 1D / 16;
                        Wz -= 1D / 16;
                  }
            }

            return new AABB(x1 - Wx, y, z1 - Wz, x1 + Wx, y + height, z1 + Wz);
      }

      // BUILDS NEW BOUNDING BOX
      protected void recalculateBoundingBox() {
            AABB box = newBox(this.pos, this.actualY, 9D / 16, direction);
            this.setPosRaw((box.minX + box.maxX) / 2, box.minY, (box.minZ + box.maxZ) / 2);
            this.setBoundingBox(box);
      }

      /** IMPLEMENTS GRAVITY WHEN HUNG BACKPACKS LOSES SUPPORTING BLOCK **/
      @Override
      public void tick() {
            super.tick();
            this.updateGravity();
            this.wobble();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.baseTick();
      }

      private void wobble() {
            Kind kind = getLocalData().kind();
            if (!Kind.UPGRADED.is(kind) && isInLava() || !Kind.METAL.is(kind) && isOnFire()) {
                  wobble += 2;
            } else
            if (wobble > 0)
                  wobble -= 1;
      }


      private void updateGravity() {
            this.setNoGravity(this.isNoGravity() && !this.level().noCollision(this, this.getBoundingBox().inflate(0.1, -0.1, 0.1)));
            boolean inLava = this.isInLava();
            Kind b$kind = getLocalData().kind();
            if (!this.isNoGravity()) {
                  if (this.isInWater()) {
                        inWaterGravity();
                  } else if (inLava) {
                        if (b$kind == Kind.UPGRADED && this.isEyeInFluid(FluidTags.LAVA) && getDeltaMovement().y < 0.1) {
                              this.setDeltaMovement(this.getDeltaMovement().add(0D, 0.02D, 0D));
                        }
                        this.setDeltaMovement(this.getDeltaMovement().scale(0.6D));
                  } else {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
                        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
                  }
            }
      }

      private void inWaterGravity() {
            AABB thisBox = this.getBoundingBox();
            AABB box = new AABB(thisBox.maxX, thisBox.maxY + 6D / 16D, thisBox.maxZ, thisBox.minX, thisBox.maxY, thisBox.minZ);
            List<Entity> entityList = this.getCommandSenderWorld().getEntities(this, box);
            if (!entityList.isEmpty()) {
                  Entity entity = entityList.get(0);
                  double velocity = this.actualY - entity.getY();
                  if (entityList.get(0) instanceof Player player) {
                        this.setDeltaMovement(0, velocity / 10, 0);
                        if (player instanceof ServerPlayer serverPlayer)
                              Services.REGISTRY.triggerSpecial(serverPlayer, SpecialCriterion.Special.HOP);
                  }
                  else if (velocity < -0.6)
                        inWaterBob();
                  else this.setDeltaMovement(0, velocity / 20, 0);
            } else inWaterBob();
      }

      private void inWaterBob() {
            if (this.isUnderWater()) {
                  this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
                  this.setDeltaMovement(this.getDeltaMovement().add(0D, 0.003D, 0D));
            } else if (this.isInWater() && getDeltaMovement().y < 0.01) {
                  this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
                  this.setDeltaMovement(this.getDeltaMovement().add(0D, -0.01D, 0D));
            }
      }

      @Override
      public boolean displayFireAnimation() {
            return this.isOnFire() && !this.isSpectator() && getLocalData().kind() != Kind.UPGRADED;
      }

      @Override
      public boolean fireImmune() {
            return getLocalData().kind() == Kind.UPGRADED || this.getType().fireImmune();
      }

      /** DATA MANAGEMENT **/
      // CLIENT
      @Override @NotNull
      public Packet<ClientGamePacketListener> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue());
      }

      @Override
      public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
            this.setDirection(Direction.from3DDataValue(packet.getData()));
      }

      // NBT
      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putByte("facing", (byte)this.direction.get3DDataValue());
            tag.put("display", getDisplay());
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);
            this.setDirection(Direction.from3DDataValue(tag.getByte("facing")));
            this.setDisplay(tag.getCompound("display"));
      }

      // LOCAL
      public CompoundTag getDisplay() {
            CompoundTag tag = new CompoundTag();
            tag.putString("key", this.entityData.get(KEY));
            tag.putInt("color", this.entityData.get(COLOR));
            tag.put("Trim", this.entityData.get(TRIM));
            Component component = this.entityData.get(HOVER_NAME);
            String json = Component.Serializer.toJson(component);
            tag.putString("hover_name", json);
            return tag;
      }

      /** COLLISIONS AND INTERACTIONS **/
      @Override
      public boolean canCollideWith(@NotNull Entity that) {
            if (that instanceof LivingEntity livingEntity && !livingEntity.isAlive())
                  return false;

            if (this.isPassengerOfSameVehicle(that))
                  return false;

            return (that.canBeCollidedWith() || that.isPushable());
      }

      @Override
      public boolean canBeCollidedWith() {
            return true;
      }

      @Override
      public boolean skipAttackInteraction(Entity attacker) {
            if (attacker instanceof Player player) {
                  return this.hurt(this.damageSources().playerAttack(player), 0.0f);
            }
            return false;
      }

      @Override
      public boolean hurt(DamageSource damageSource, float amount) {
            if ((damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.ON_FIRE) || damageSource.is(DamageTypes.LAVA)) && this.fireImmune())
                  return false;
            double height = 0.1D;
            if (damageSource.is(DamageTypes.EXPLOSION) || damageSource.is(DamageTypes.PLAYER_EXPLOSION)) {
                  height += Math.sqrt(amount) / 20;
                  return hop(height);
            }
            if (damageSource.is(DamageTypes.ARROW) || damageSource.is(DamageTypes.THROWN) || damageSource.is(DamageTypes.TRIDENT) || damageSource.is(DamageTypes.MOB_PROJECTILE)) {
                  hop(height);
                  return false;
            }
            if (damageSource.is(DamageTypes.PLAYER_ATTACK) && damageSource.getDirectEntity() instanceof Player player) {
                  if (player.isCreative()) {
                        this.kill();
                        this.markHurt();
                  }
                  else {
                        float damage = player.getUUID() == placedBy ? .8f : .5f;
                        wobble += (int) (BREAK_TIMER * damage);
                        if (wobble > BREAK_TIMER) {
                              breakAndDropContents();
                              return true;
                        }
                        else {
                              PlaySound.HIT.at(this, getKind());
                              return hop(height);
                        }
                  }
            }

            hop(height);
            return true;
      }

      private void breakAndDropContents() {
            PlaySound.BREAK.at(this, getKind());
            boolean dropItems = level().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
            if (dropItems) {
                  while (!this.getItemStacks().isEmpty()) {
                        ItemStack stack = this.getItemStacks().remove(0);
                        this.spawnAtLocation(stack);
                  }
            }
            ItemStack backpack = toStack(this);
            if (!this.isRemoved() && !this.level().isClientSide()) {
                  this.kill();
                  this.markHurt();
                  if (dropItems) this.spawnAtLocation(backpack);
            }
      }

      public boolean hop(double height) {
            if (this.isNoGravity())
                  this.setNoGravity(false);
            else {
                  this.setDeltaMovement(this.getDeltaMovement().add(0.0D, height, 0.0D));
            }
            return true;
      }

      // PREFORMS THIS ACTION WHEN IT IS RIGHT-CLICKED
      @Override @NotNull
      public InteractionResult interact(Player player, InteractionHand hand) {
            InteractionResult interact = interact(player);
            if (interact.consumesAction())
                  return interact;

            if (viewable.viewers < 1)
                  PlaySound.OPEN.at(this, getKind());
            Services.NETWORK.openBackpackMenu(player, this);
            return InteractionResult.SUCCESS;
      }

      public InteractionResult interact(Player player) {
            BackData backData = BackData.get(player);
            boolean actionKeyPressed = backData.actionKeyPressed;
            ItemStack backStack = backData.getStack();
            ItemStack handStack = player.getMainHandItem();
            ItemStack backpackStack = actionKeyPressed ? backStack : handStack;

            if (Kind.isBackpack(backpackStack))
                  return BackpackItem.useOnBackpack(player, this, backpackStack, actionKeyPressed);

            if (actionKeyPressed) {
                  if (!backData.isEmpty() && !this.isRemoved())
                  {
                        PlaySound.HIT.at(this, this.getKind());
                        this.hop(.1);
                  }
                  else
                  {
/*                  Equips Backpack only if...
                      - damage source is player.
                      - player is not creative.
                      - backSlot is not occupied */
                        NonNullList<ItemStack> playerInventoryStacks = BackData.get(player).backpackInventory.getItemStacks();
                        NonNullList<ItemStack> backpackEntityStacks = this.getItemStacks();
                        playerInventoryStacks.clear();
                        playerInventoryStacks.addAll(backpackEntityStacks);
                        backData.set(toStack(this));
                        PlaySound.EQUIP.at(player, this.getKind());
                        if (player instanceof ServerPlayer serverPlayer)
                              Services.NETWORK.backpackInventory2C(serverPlayer);

                        if (!this.isRemoved() && !player.level().isClientSide())
                        {
                              this.kill();
                              this.markHurt();
                        }
                  }
                  return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
      }

      @Override
      public ItemStack getPickResult() {
            return toStack(this);
      }
      /** REQUIRED FEILDS **/
      @Override
      protected boolean repositionEntityAfterLoad() {
            return false;
      }

      @Override
      public boolean isPickable() {
            return true;
      }

      @Override @NotNull
      public Vec3 position() {
            return new Vec3(this.pos.getX(), this.actualY, this.pos.getZ());
      }

}
