package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.*;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.network.clientbound.SendEnderStacks;
import com.beansgalaxy.backpacks.network.clientbound.SyncBackInventory;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.PlayerTeam;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class EntityAbstract extends Backpack {
      public static final EntityDataAccessor<Boolean> LOCKED = SynchedEntityData.defineId(EntityAbstract.class, EntityDataSerializers.BOOLEAN);
      public Direction direction;
      protected BlockPos pos;
      private double actualY;
      private static final int BREAK_TIMER = 25;
      public int wobble = 12;
      public int breakAmount = 0;
      private CompoundTag itemTags;

      // REGISTER BACKPACK CONSTRUCTOR
      public EntityAbstract(EntityType<? extends Entity> type, Level level) {
            super(type, level);
            this.blocksBuilding = true;
      }

      public EntityAbstract(EntityType<? extends Entity> type, Level level, int x, double y, int z) {
            super(type, level);
            this.blocksBuilding = true;
            this.actualY = y;
            this.pos = BlockPos.containing(x, y, z);
      }

      @Nullable
      public static EntityAbstract create(ItemStack backpackStack, int x, double y, int z, float yaw, boolean onDeath,
                                          Direction direction, Player player, NonNullList<ItemStack> stacks)
      {
            Traits.LocalData traits = Traits.LocalData.fromStack(backpackStack, player);
            EntityAbstract entityAbstract = create(backpackStack, x, y, z, yaw, onDeath, direction, player.level(), player.getUUID(), stacks);
            if (entityAbstract != null && player instanceof ServerPlayer serverPlayer) {
                  Services.REGISTRY.triggerPlace(serverPlayer, traits.backpack_id);
                  player.level().gameEvent(player, GameEvent.ENTITY_PLACE, entityAbstract.position());
            }

            return entityAbstract;
      }

      @Nullable
      public static EntityAbstract create(ItemStack backpackStack, int x, double y, int z, float yaw, boolean onDeath,
                                          Direction direction, Level level, UUID uuid, NonNullList<ItemStack> stacks)
      {
            Traits.LocalData traits = Traits.LocalData.fromStack(backpackStack, level, uuid);
            if (traits == null)
                  return null;

            boolean empty = traits.isEmpty();
            if (empty)
                  return null;

            EntityAbstract backpack;
            Optional<UUID> placedBy = Optional.of(uuid);
            if (backpackStack.getItem() instanceof EnderBackpack ender) {
                  if (onDeath && !ender.isPersistent(backpackStack) && ServerSave.GAMERULES.get(Gamerules.UNBIND_ENDER_ON_DEATH))
                        placedBy = Optional.empty();
                  else
                        placedBy = Optional.of(ender.getOrCreateUUID(uuid, level, backpackStack));

                  backpack = new EntityEnder(level);
            }
            else if (backpackStack.getItem() instanceof WingedBackpack) {
                  int damage = backpackStack.getDamageValue();
                  backpack = new EntityFlight(level, stacks, damage);
            }
            else backpack = new EntityGeneral(level, stacks);

            backpack.setPlacedBy(placedBy);

            backpack.actualY = y;
            backpack.pos = BlockPos.containing(x, y, z);

            backpack.setDirection(direction);
            backpack.entityData.set(LOCAL_DATA, traits.toNBT());
            backpack.saveToItemTag(backpackStack.getTag());

            Component hoverName = backpackStack.hasCustomHoverName() ? backpackStack.getHoverName(): null;
            backpack.setCustomName(hoverName);

            boolean isHorizontal = direction.getAxis().isHorizontal();
            if (!isHorizontal) backpack.setYRot(yaw);

            PlaySound.PLACE.at(backpack, Kind.fromStack(backpackStack));
            if (level instanceof ServerLevel) {
                  level.addFreshEntity(backpack);
            }

            if (isHorizontal) level.updateNeighbourForOutputSignal(backpack.pos, Blocks.AIR);

            backpackStack.shrink(1);
            return backpack;
      }

      public void setPlacedBy(Optional<UUID> placedBy) {
            placedBy.ifPresent(in -> entityData.set(OWNER, placedBy));
      }

      @Override
      public Viewable getViewable() {
            return getInventory().getViewable();
      }

      private void saveToItemTag(@Nullable CompoundTag tag) {
            if (tag == null)
                  return;

            CompoundTag copy = tag.copy();
            if (copy.contains("Locked")) {
                  entityData.set(LOCKED, copy.getBoolean("Locked"));
                  copy.remove("Locked");
            }

            copy.remove("backpack_id");
            copy.remove("Trim");
            if (copy.contains("display")) {
                  CompoundTag display = copy.getCompound("display");
                  display.remove("color");
                  if (display.isEmpty())
                        copy.remove("display");
            }
            this.itemTags = copy;
      }

      @Override
      protected void defineSynchedData() {
            this.entityData.define(LOCKED, false);
            super.defineSynchedData();
      }

      public ItemStack toStack() {
            Traits.LocalData traits = this.getTraits();
            Kind kind = traits.kind;
            if (Kind.UPGRADED.is(kind))
                  kind = Kind.METAL;
            Item item = kind.getItem();
            ItemStack stack = item.getDefaultInstance();
            CompoundTag itemTags = this.itemTags;
            if (itemTags != null)
                  stack.setTag(itemTags);

            boolean locked = this.isLocked();
            if (locked) {
                  stack.getOrCreateTag().putBoolean("Locked", true);
            }

            UUID placedBy = this.getPlacedBy();
            if (item instanceof EnderBackpack enderBackpack && placedBy != null) {
                  EnderInventory enderData = EnderStorage.getEnderData(placedBy, this.level());
                  boolean enderLocked = enderData.isLocked();
                  if (!enderLocked) enderBackpack.setUUID(placedBy, stack);
            } else {
                  CompoundTag trim = traits.getTrim();
                  if (!trim.isEmpty())
                        stack.addTagElement("Trim", trim);
            }

            int color = traits.color;
            boolean hasDefaultColor = false;

            switch (kind) {
                  case METAL, UPGRADED -> {
                        String key = traits.backpack_id;
                        if (!Constants.isEmpty(key) && !key.equals("iron")) // TODO: 20.1-0.18-v2 REMOVE KEY EQUALS IRON CHECK
                              stack.getOrCreateTag().putString("backpack_id", key);
                  }
                  case LEATHER -> hasDefaultColor = color == DEFAULT_COLOR;
                  case WINGED -> hasDefaultColor = color == WingedBackpack.WINGED_ENTITY;
            }

            if (!hasDefaultColor && stack.getItem() instanceof DyableBackpack)
                  stack.getOrCreateTagElement("display").putInt("color", color);


            Component customName = this.getCustomName();
            if (customName != null)
                  stack.setHoverName(customName);
            else {
                  stack.resetHoverName();
            }

            return stack;
      }

      public abstract BackpackInventory getInventory();

      public NonNullList<ItemStack> getItemStacks() {
            return getInventory().getItemStacks();
      }

      @Override
      public Component getName() {
            Traits.LocalData traits = getTraits();
            return traits.kind.getName(traits);
      }

      @Override
      public Component getDisplayName() {
            Component customName = this.getCustomName() == null ? getName() : getCustomName();
            return PlayerTeam.formatNameForTeam(this.getTeam(), customName).withStyle(($$0) ->
                        $$0.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID()));
      }

      @Override
      public float getNameTagOffsetY() {
            return onGround() || direction.getAxis().isVertical() ? 13/16f : 3/16f;
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
            if (wobble > 0)
                  wobble--;
            else breakAmount = 0;
      }

      @Override
      public void setRemainingFireTicks(int i) {
            int fireTicks = level().isClientSide ? i : Math.min(i, 30);
            super.setRemainingFireTicks(fireTicks);
      }

      private void updateGravity() {
            this.setNoGravity(this.isNoGravity() && !this.level().noCollision(this, this.getBoundingBox().inflate(0.1, -0.1, 0.1)));
            boolean inLava = this.isInLava();
            if (!this.isNoGravity()) {
                  if (this.isInWater()) {
                        inWaterGravity();
                  } else if (inLava) {
                        if (getTraits().fireResistant() && this.isEyeInFluid(FluidTags.LAVA) && getDeltaMovement().y < 0.1) {
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
            return false;
      }

      @Override
      public boolean fireImmune() {
            return getTraits().fireResistant();
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
      protected void readAdditionalSaveData(CompoundTag tag) {
            getInventory().readStackNbt(tag);
            fromNBT(tag);
      }

      protected void fromNBT(CompoundTag tag) {
            this.setDirection(Direction.from3DDataValue(tag.getByte("facing")));
            if (tag.contains("display")) // TODO: FOR REMOVAL 20.1-0.18-v2 : DISPLAY TAG IS THE OLD METHOD FOR SAVING
                  this.setDisplay(tag.getCompound("display"));
            else {
                  this.setNoGravity(tag.getBoolean("hanging"));
                  entityData.set(LOCKED, tag.getBoolean("Locked"));
                  CompoundTag localData = tag.getCompound("local_data");
                  this.entityData.set(OWNER, Optional.of(localData.getUUID("owner")));
                  entityData.set(LOCAL_DATA, localData);
                  itemTags = tag.getCompound("item_tags");
            }

      }

      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
            getInventory().writeNbt(tag);
            toNBT(tag);
      }

      protected void toNBT(CompoundTag tag) {
            tag.putBoolean("Locked", entityData.get(LOCKED));
            tag.putByte("facing", (byte)this.direction.get3DDataValue());
            tag.putBoolean("hanging", this.isNoGravity());
            CompoundTag traits = getTraits().toNBT();
            UUID placedBy = getPlacedBy();
            if (placedBy != this.uuid && placedBy != null)
                  traits.putUUID("owner", placedBy);
            tag.put("local_data", traits);
            if (itemTags != null)
                  tag.put("item_tags", itemTags);
      }

      // LOCAL
      @Deprecated // 20.1-0.18-v2
      public void setDisplay(CompoundTag display) {
            String key = display.getString("key");
            int color = display.getInt("color");
            CompoundTag trim = display.getCompound("Trim");
            MutableComponent hoverName = Component.Serializer.fromJson(display.getString("hover_name"));

            Kind kind = Kind.METAL;
            switch (key) {
                  case "iron" -> key = "";
                  case "netherite" -> key = "null";
                  case "leather" -> {
                        key = "";
                        kind = Kind.LEATHER;
                  }
                  case "ender" -> {
                        key = "";
                        kind = Kind.ENDER;
                  }
                  case "winged" -> {
                        key = "";
                         kind = Kind.WINGED;
                  }
            }
            Traits.LocalData traits = new Traits.LocalData(key, kind, color, trim, hoverName);
            this.traits = traits;
            this.entityData.set(LOCAL_DATA, traits.toNBT());

            if (!display.contains("placed_by"))
                  Constants.LOG.warn("No \"Placed By\" UUID provided from -> " + this);
            else
                  this.entityData.set(OWNER, Optional.of(display.getUUID("placed_by")));
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
            double height = 0.1D;

            if ((damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.LAVA))) {
                  if (fireImmune())
                        return false;
                  damage(1, true);
                  if ((breakAmount + 10) % 11 == 0)
                        playSound(SoundEvents.GENERIC_BURN, 0.8f, 1f);
                  return true;
            }
            if (damageSource.is(DamageTypes.ON_FIRE))
                  return false;
            if (damageSource.is(DamageTypes.CACTUS)) {
                  breakAndDropContents();
                  return true;
            }
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
                        float damage = player.getUUID().equals(getPlacedBy()) || !isLocked()
                                    ? 10
                                    : 3;
                        return damage((int) (damage), false);
                  }
            }

            hop(height);
            return true;
      }

      private boolean damage(int damage, boolean silent) {
            int health = 24;
            wobble = Math.min(wobble + 10, health);

            breakAmount += damage;
            if (breakAmount >= health)
                  breakAndDropContents();
            else if (!silent)
            {
                  PlaySound.HIT.at(this, getTraits().kind);
                  return hop(0.1);
            }
            return true;
      }

      private void breakAndDropContents() {
            Kind kind = getTraits().kind;
            PlaySound.BREAK.at(this, kind);
            boolean dropItems = level().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
            if (dropItems && !Kind.ENDER.is(kind)) {
                  NonNullList<ItemStack> stacks = getInventory().getItemStacks();
                  while (!stacks.isEmpty()) {
                        ItemStack stack = stacks.remove(0);
                        this.spawnAtLocation(stack);
                  }
            }
            ItemStack backpack = toStack();
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
            if (level().isClientSide())
                  getViewable().headPitch += 0.1f;
            return true;
      }

      public enum LockedReason {
            IS_REMOVED((backData, level, entityAbstract) ->
                        entityAbstract.isRemoved()),
            INVALID_DATA((backData, level, entityAbstract) -> {
                  if (entityAbstract.getPlacedBy() == null || entityAbstract.getInventory() == null) {
                        UUID uuid = backData.owner.getUUID();
                        entityAbstract.setPlacedBy(Optional.of(uuid));
                  }
                  return false;
            }),
            IS_LOCKED(((backData, serverLevel, entityAbstract) -> {
                  if (backData.owner.getUUID().equals(entityAbstract.getPlacedBy()))
                        return false;
                  return entityAbstract.isLocked();
            })),
            NOT_OWNER((backData, level, entityAbstract) -> {
                  if (ServerSave.GAMERULES.get(Gamerules.LOCK_BACKPACK_NOT_OWNER)) {
                        UUID placedBy = entityAbstract.getPlacedBy();
                        return backData.owner.getUUID() != placedBy || placedBy == entityAbstract.uuid;
                  }
                  return false;
            }),
            OWNER_OFFLINE((backData, level, entityAbstract) -> {
                  if (ServerSave.GAMERULES.get(Gamerules.LOCK_BACKPACK_OFFLINE)) {
                        UUID placedBy = entityAbstract.getPlacedBy();
                        return placedBy != null && placedBy != entityAbstract.uuid && level.getPlayerByUUID(placedBy) == null;
                  }
                  return false;
            }),
            ENDER_OFFLINE((backData, level, entityAbstract) -> {
                  if (ServerSave.GAMERULES.get(Gamerules.LOCK_ENDER_OFFLINE)) {
                        return entityAbstract instanceof EntityEnder && OWNER_OFFLINE.apply(backData, level, entityAbstract);
                  }
                  return false;
            });

            final TriFunction<BackData, ServerLevel, EntityAbstract, Boolean> isLocked;
            LockedReason(TriFunction<BackData, ServerLevel, EntityAbstract, Boolean> isLocked) {
                  this.isLocked = isLocked;
            }

            public boolean apply(BackData backData, ServerLevel player, EntityAbstract backpack) {
                  return isLocked.apply(backData, player, backpack);
            }
      }

      protected boolean isLocked() {
            return entityData.get(LOCKED);

      }

      public boolean isLocked(BackData backData, ServerLevel level, Kind kind) {
            Player player = backData.owner;
            if (player.isCreative())
                  return false;

            for (LockedReason value : LockedReason.values()) {
                  if (value.apply(backData, level, this)) {
                        player.displayClientMessage(Component.translatable("entity.beansbackpacks.locked." + value.toString().toLowerCase()), true);
                        PlaySound.HIT.at(this, kind);
                        this.hop(.1);
                        return true;
                  }
            }
            return false;
      }

      public boolean mayPickup(BackData backData, Kind kind) {
            List<ItemStack> disabling = backData.getDisabling();
            if (!backData.isEmpty())
                  disabling.add(backData.getStack());

            Iterator<ItemStack> iterator = disabling.iterator();
            if (iterator.hasNext()) {
                  PlaySound.HIT.at(this, kind);
                  this.hop(.1);
                  MutableComponent items = Constants.getName(iterator.next());
                  while (iterator.hasNext()) {
                        ItemStack stack = iterator.next();
                        items.append(iterator.hasNext() ? Component.literal(", ") : Component.translatable("entity.beansbackpacks.blocked.and"));
                        items.append(Constants.getName(stack));
                  }
                  MutableComponent message = Component.translatable("entity.beansbackpacks.blocked.hotkey_equip", items).withStyle(ChatFormatting.WHITE);
                  backData.owner.displayClientMessage(message, true);
                  return false;
            }

            return true;
      }

      // PREFORMS THIS ACTION WHEN IT IS RIGHT-CLICKED
      @Override @NotNull
      public InteractionResult interact(Player player, InteractionHand hand) {
            if (player.isDiscrete())
                  return shiftClickOnBackpack(player, hand);
            else if (player instanceof ServerPlayer serverPlayer) {
                  InteractionResult interact = interact(serverPlayer);
                  if (interact.consumesAction())
                        return interact;

                  if (getViewable().getViewers() < 1)
                        PlaySound.OPEN.at(this, getTraits().kind);
                  if (Kind.ENDER.is(traits.kind))
                        SendEnderStacks.send(serverPlayer, getPlacedBy());

                  Services.NETWORK.openBackpackMenu(player, this);
            }

            return InteractionResult.SUCCESS;
      }

      private InteractionResult shiftClickOnBackpack(Player player, InteractionHand hand) {
            BackData backData = BackData.get(player);
            ItemStack backStack = backData.getStack();
            ItemStack inHand = player.getItemInHand(hand);

            ItemStack backpackStack = backData.actionKeyPressed && !backStack.isEmpty() ? backStack : inHand;
            if (Kind.isBackpack(backpackStack))
                  return BackpackItem.useOnBackpack(player, this, backpackStack, backData.actionKeyPressed);

            Vec3 vec3 = player.getEyePosition();
            Vec3 view = player.getViewVector(1).scale(10);
            AABB aabb = new AABB(player.blockPosition(), this.blockPosition()).inflate(1);
            Predicate<Entity> entityPredicate = (p_234237_) -> !p_234237_.isSpectator() && p_234237_.isPickable();
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(player, vec3, vec3.add(view), aabb, entityPredicate, 10);
            if (hitResult == null) return InteractionResult.PASS;

            Vec3 hit = hitResult.getLocation();
            Vec3 target = new Vec3(this.getBlockX() + 0.5, this.actualY, this.getBlockZ() + 0.5);
            Direction entityDirection = this.getDirection();
            Direction.Axis axis = entityDirection.getAxis();
            boolean isHorizontal = axis.isHorizontal();
            if (isHorizontal)
                  target = target.relative(entityDirection, -0.5);

            double x = target.x - hit.x;
            double y = hit.y - target.y;
            double z = target.z - hit.z;

            Direction direction;
            if ((axis == Direction.Axis.X && x == 0) || (axis == Direction.Axis.Z && z == 0))
                  direction = entityDirection.getOpposite();
            else if (y == 9.0/16)
                  direction = Direction.UP;
            else if (y == 0)
                  direction = Direction.DOWN;
            else if (Math.abs(x) > Math.abs(z))
                  direction = x < 0 ? Direction.EAST: Direction.WEST;
            else
                  direction = z < 0 ? Direction.SOUTH: Direction.NORTH;
            //this.level().addParticle(ParticleTypes.BUBBLE_POP, target.x - x, y + target.y, target.z - z, 0, 0, 0);

            Vec3 placeVec = this.position().add(0, y, 0).relative(direction, 1);
            BlockPos placePos = BlockPos.containing(placeVec.x, placeVec.y, placeVec.z);
            if (!player.level().getBlockState(placePos).canBeReplaced())
                  return InteractionResult.PASS;

            BlockHitResult blockHitResult = new BlockHitResult(placeVec, direction, placePos, true);
            return inHand.useOn(new UseOnContext(player, hand, blockHitResult));
      }

      public InteractionResult interact(ServerPlayer player) {
            Kind kind = getTraits().kind;
            BackData backData = BackData.get(player);
            boolean actionKeyPressed = backData.actionKeyPressed;
            if (isLocked(backData, player.serverLevel(), kind))
                  return InteractionResult.SUCCESS;

            if (actionKeyPressed) {
                  ItemStack backpackStack = toStack();
                  if (backData.mayEquip(backpackStack, false))
                  {
                        if (this instanceof EntityEnder ender) {
                              if (ender.getPlacedBy() == null) {
                                    ender.setPlacedBy(Optional.of(player.getUUID()));
                                    EnderStorage.getEnderData(player);
                              }
                        } else {
                              BackpackInventory backpackInventory = BackData.get(player).getBackpackInventory();
                              NonNullList<ItemStack> playerInventoryStacks = backpackInventory.getItemStacks();
                              NonNullList<ItemStack> backpackEntityStacks = this.getItemStacks();
                              backpackInventory.clearContent();
                              playerInventoryStacks.addAll(backpackEntityStacks);
                        }
                        backData.set(backpackStack);
                        PlaySound.EQUIP.at(player, kind);
                        SyncBackInventory.send(player);

                        if (!this.isRemoved())
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
            return toStack();
      }

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

      public int getAnalogOutput() {
            if (getItemStacks().isEmpty())
                  return 0;

            int space = getInventory().spaceLeft();
            int max = getTraits().maxStacks() * 64;

            if (space == max)
                  return 0;

            float i = max - space;
            i /= max;
            i *= 14;
            i += 1;

            return (int) i;
      }
}
