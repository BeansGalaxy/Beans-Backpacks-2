package com.beansgalaxy.backpacks.entity;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.*;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public abstract class EntityAbstract extends Backpack {
      public Direction direction;
      protected BlockPos pos;
      private double actualY;
      private static final int BREAK_TIMER = 25;
      public int wobble = 9;
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
            Traits.LocalData traits = Traits.LocalData.fromStack(backpackStack);
            if (traits == null)
                  return null;

            boolean empty = traits.isEmpty();
            if (empty)
                  return null;

            EntityAbstract backpack;
            if (backpackStack.getItem() instanceof EnderBackpack ender) {
                  Optional<UUID> uuid;
                  if (onDeath && !ender.isPersistent(backpackStack) && ServerSave.CONFIG.get(Gamerules.UNBIND_ENDER_ON_DEATH))
                        uuid = Optional.empty();
                  else
                        uuid = Optional.of(ender.getOrCreateUUID(player.getUUID(), backpackStack));

                  backpack = new EntityEnder(player, uuid);
            }
            else if (backpackStack.getItem() instanceof WingedBackpack) {
                  int damage = backpackStack.getDamageValue();
                  backpack = new EntityFlight(player, stacks, damage);
            }
            else backpack = new EntityGeneral(player, stacks);

            backpack.actualY = y;
            backpack.pos = BlockPos.containing(x, y, z);

            backpack.setDirection(direction);
            backpack.entityData.set(LOCAL_DATA, traits.toNBT());
            backpack.saveToItemTag(backpackStack.getTag());

            Component hoverName = backpackStack.hasCustomHoverName() ? backpackStack.getHoverName(): null;
            backpack.setCustomName(hoverName);

            boolean isHorizontal = direction.getAxis().isHorizontal();
            if (!isHorizontal) backpack.setYRot(yaw);

            Level level = player.level();
            PlaySound.PLACE.at(backpack, Kind.fromStack(backpackStack));
            if (player instanceof ServerPlayer serverPlayer) {
                  Services.REGISTRY.triggerPlace(serverPlayer, traits.key);
                  level.gameEvent(player, GameEvent.ENTITY_PLACE, backpack.position());
                  level.addFreshEntity(backpack);
            }

            if (isHorizontal) level.updateNeighbourForOutputSignal(backpack.pos, Blocks.AIR);

            backpackStack.shrink(1);
            return backpack;
      }

      private void saveToItemTag(@Nullable CompoundTag tag) {
            if (tag == null)
                  return;

            CompoundTag copy = tag.copy();
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
            super.defineSynchedData();
      }

      public static ItemStack toStack(EntityAbstract backpack) {
            Traits.LocalData traits = backpack.getTraits();
            Kind kind = traits.kind;
            if (Kind.UPGRADED.is(kind))
                  kind = Kind.METAL;
            Item item = kind.getItem();
            ItemStack stack = item.getDefaultInstance();
            CompoundTag itemTags = backpack.itemTags;
            if (itemTags != null)
                  stack.setTag(itemTags);

            if (item instanceof EnderBackpack enderBackpack && backpack.getPlacedBy() != null) {
                  enderBackpack.setUUID(backpack.getPlacedBy(), stack);
            } else {
                  CompoundTag trim = traits.getTrim();
                  if (!trim.isEmpty())
                        stack.addTagElement("Trim", trim);
            }

            int color = traits.color;
            boolean hasDefaultColor = false;

            switch (kind) {
                  case METAL, UPGRADED -> {
                        String key = traits.key;
                        if (!Constants.isEmpty(key) && !key.equals("iron")) // TODO: 20.1-0.18-v2 REMOVE KEY EQUALS IRON CHECK
                              stack.getOrCreateTag().putString("backpack_id", key);
                  }
                  case LEATHER -> hasDefaultColor = color == DEFAULT_COLOR;
                  case WINGED -> hasDefaultColor = color == WingedBackpack.WINGED_ENTITY;
            }

            if (!hasDefaultColor && stack.getItem() instanceof DyableBackpack)
                  stack.getOrCreateTagElement("display").putInt("color", color);


            Component customName = backpack.getCustomName();
            if (customName != null)
                  stack.setHoverName(customName);
            else {
                  stack.resetHoverName();
            }

            return stack;
      }

      public abstract BackpackInventory getInventory();

      abstract NonNullList<ItemStack> getItemStacks();

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
            boolean inLava = isInLava();
            boolean isOnFire = isOnFire();
            boolean fireResistant = getTraits().fireResistant();
            if (!fireResistant && inLava || isOnFire)
            {
                  if (wobble % 12 == 0)
                        playSound(SoundEvents.GENERIC_BURN);
                  damage(1, true);
            }
            else if (wobble > 0)
                  wobble -= 1;
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
                  if (tag.contains("hanging")) // TODO: FOR REMOVAL 20.1-0.19-v2 : BACKPACK WITHOUT "hanging" TAG WILL FALL
                        this.setNoGravity(tag.getBoolean("hanging"));
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
            if ((damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.ON_FIRE) || damageSource.is(DamageTypes.LAVA))) {
                  if (fireImmune())
                        return false;
                  height = 0;
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
                        float damage = player.getUUID() == getPlacedBy() ? .8f : .5f;
                        return damage((int) (BREAK_TIMER * damage), false);
                  }
            }

            hop(height);
            return true;
      }

      private boolean damage(int damage, boolean silent) {
            wobble += damage;
            if (wobble > BREAK_TIMER)
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
            if (level().isClientSide())
                  getViewable().headPitch += 0.1f;
            return true;
      }

      public enum LockedReason {
            ENDER_NO_OWNER(null, (player, entityAbstract) ->
                        entityAbstract instanceof EntityEnder ender && ender.getPlacedBy() == null),
            NOT_OWNER(Gamerules.LOCK_BACKPACK_NOT_OWNER, (player, entityAbstract) -> {
                  UUID playerUUID = player.getUUID();
                  UUID placedBy = entityAbstract.getPlacedBy();
                  return playerUUID != placedBy || placedBy == entityAbstract.uuid;
            }),
            OWNER_OFFLINE(Gamerules.LOCK_BACKPACK_OFFLINE, (player, entityAbstract) -> {
                  UUID placedBy = entityAbstract.getPlacedBy();
                  return placedBy != entityAbstract.uuid && player.serverLevel().getPlayerByUUID(placedBy) == null;
            }),
            ENDER_OFFLINE(Gamerules.ENDER_LOCK_LOGGED_OFF, (player, entityAbstract) ->
                        entityAbstract instanceof EntityEnder && OWNER_OFFLINE.apply(player, entityAbstract));

            final BiFunction<ServerPlayer, EntityAbstract, Boolean> isLocked;
            final Gamerules gamerule;

            LockedReason(Gamerules gamerule, BiFunction<ServerPlayer, EntityAbstract, Boolean> isLocked) {
                  this.gamerule = gamerule;
                  this.isLocked = isLocked;
            }

            public boolean apply(ServerPlayer player, EntityAbstract backpack) {
                  return isLocked.apply(player, backpack);
            }
      }

      public boolean isLocked(ServerPlayer player, Kind kind) {
            if (player.isCreative())
                  return false;

            for (LockedReason value : LockedReason.values()) {
                  boolean enabled = value.gamerule == null || ServerSave.CONFIG.get(value.gamerule);
                  if (enabled && value.apply(player, this)) {
                        player.displayClientMessage(Component.translatable("entity.beansbackpacks.locked." + value.toString().toLowerCase()), true);
                        PlaySound.HIT.at(this, kind);
                        this.hop(.1);
                        return true;
                  }
            }
            return false;
      }

      // PREFORMS THIS ACTION WHEN IT IS RIGHT-CLICKED
      @Override @NotNull
      public InteractionResult interact(Player player, InteractionHand hand) {
            if (player instanceof ServerPlayer serverPlayer) {
                  InteractionResult interact = interact(serverPlayer);
                  if (interact.consumesAction())
                        return interact;

                  if (viewable.viewers < 1)
                        PlaySound.OPEN.at(this, getTraits().kind);
                  Services.NETWORK.openBackpackMenu(player, this);
            }
            return InteractionResult.SUCCESS;
      }

      public InteractionResult interact(ServerPlayer player) {
            Kind kind = getTraits().kind;
            if (isLocked(player, kind))
                  return InteractionResult.SUCCESS;

            BackData backData = BackData.get(player);
            boolean actionKeyPressed = backData.actionKeyPressed;
            ItemStack backStack = backData.getStack();
            ItemStack handStack = player.getMainHandItem();
            ItemStack backpackStack = actionKeyPressed ? backStack : handStack;

            if (Kind.isBackpack(backpackStack))
                  return BackpackItem.useOnBackpack(player, this, backpackStack, actionKeyPressed);

            if (actionKeyPressed) {
                  boolean b = !backData.isEmpty();
                  boolean b1 = backData.backSlotDisabled();
                  boolean b2 = this.isRemoved();
                  if (b || b1 || b2)
                  {
                        PlaySound.HIT.at(this, kind);
                        this.hop(.1);
                  }
                  else
                  {
/*                  Equips Backpack only if...
                      - damage source is player.
                      - backSlot is not occupied */
                        if (this instanceof EntityEnder ender) {
                              if (ender.getPlacedBy() == null) {
                                    ender.setPlacedBy(Optional.of(player.getUUID()));
                                    EnderStorage.getEnderData(player);
                              }
                        } else {
                              NonNullList<ItemStack> playerInventoryStacks = BackData.get(player).backpackInventory.getItemStacks();
                              NonNullList<ItemStack> backpackEntityStacks = this.getItemStacks();
                              playerInventoryStacks.clear();
                              playerInventoryStacks.addAll(backpackEntityStacks);
                        }
                        backData.set(toStack(this));
                        PlaySound.EQUIP.at(player, kind);
                        Services.NETWORK.backpackInventory2C(player);

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
