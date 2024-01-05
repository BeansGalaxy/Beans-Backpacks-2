package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.entity.Backpack;
import com.beansgalaxy.backpacks.entity.MobileData;
import com.beansgalaxy.backpacks.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackMenu extends AbstractContainerMenu {
      private static final ResourceLocation BACKPACK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
      private static final ResourceLocation INPUT = new ResourceLocation("sprites/empty_slot_input_large");
      private static int FIRST_SLOT_INDEX;
      public final BackpackInventory backpackInventory;
      protected final Backpack mirror;
      protected final Player viewer;
      protected final Entity owner;
      protected final BlockPos ownerPos;
      protected final float ownerYaw;
      private final int max_stacks;
      public int invOffset = 108;
      // SERVER CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInventory, BackpackInventory backpackInventory) {
            super(Services.REGISTRY.getMenu(), id);
            this.backpackInventory = backpackInventory;
            this.owner = backpackInventory.getOwner();
            this.ownerPos = owner.blockPosition();
            this.ownerYaw = owner.getVisualRotationYInDegrees();
            this.viewer = playerInventory.player;
            this.mirror = createMirror(playerInventory.player.level());
            this.max_stacks = backpackInventory.getMaxStacks();
            createInventorySlots(playerInventory);
            FIRST_SLOT_INDEX = slots.size();
            createBackpackSlots(backpackInventory);
      }

      private Backpack createMirror(Level level) {
            Backpack backpack = new Backpack(level) {

                  @Override
                  public MobileData getData() {
                        return BackpackMenu.this.backpackInventory.getData();
                  }

                  @Override
                  public BackpackInventory getBackpackInventory() {
                        return BackpackMenu.this.backpackInventory;
                  }
            };
            return backpack;
      }

      // CLIENT CONSTRUCTOR
      public BackpackMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
            this(id, playerInv, getBackpackInventory(buf.readInt(), playerInv.player.level()));
      }

      public static BackpackInventory getBackpackInventory(int entityId, Level level) {
            Entity entity = level.getEntity(entityId);
            return BackpackInventory.get(entity);
      }


      private void createBackpackSlots(Container inventory) {
            final int columns = Math.min(5 + (max_stacks / 4), 11);
            final int rows = 4;
            final int spacing = 17;
            int bpCenter = (columns / 2) * spacing;
            int x = 80 - bpCenter;
            x += spacing / 2 * -((columns % 2) - 1);
            int y = invOffset - rows * spacing + 35;


            for(int r = 0; r < rows; ++r)
                  for(int c = 0; c < columns; ++c)
                        this.addSlot(new Slot(inventory, c + r * columns, x + c * spacing, y + r * spacing) {
                              public boolean mayPlace(ItemStack p_40231_) {
                                    return false;
                              }
                        });
      }

      private void createInventorySlots(Inventory playerInventory) {
            for(int l = 0; l < 3; ++l) {
                  for(int k = 0; k < 9; ++k) {
                        this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51 + invOffset));
                  }
            }
            for(int i1 = 0; i1 < 9; ++i1) {
                  this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 109 + invOffset));
            }
      }

      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
            if (slotIndex >= FIRST_SLOT_INDEX && actionType != ClickType.QUICK_MOVE) {
                  ItemStack cursorStack = this.getCarried();
                  if (button == 0 && !cursorStack.isEmpty()) {
                        this.setRemoteCarried(cursorStack);
                        ItemStack stack = backpackInventory.insertItem(cursorStack);
                        this.setCarried(stack);
                        return;
                  }
                  if (button == 1 && !cursorStack.isEmpty()) {
                        this.setRemoteCarried(cursorStack);
                        ItemStack stack = backpackInventory.insertItem(cursorStack, 1);
                        this.setCarried(stack);
                        return;
                  }
            }
            super.clicked(slotIndex, button, actionType, player);
      }

      @Override
      public ItemStack quickMoveStack(Player player, int slotId) {
            Slot clickedSlot = slots.get(slotId);
            ItemStack clickedStack = clickedSlot.getItem();
            if (clickedStack == ItemStack.EMPTY)
                  return ItemStack.EMPTY;
            if (slotId < FIRST_SLOT_INDEX) { // HANDLES INSERT TO BACKPACK
                  if (backpackInventory.spaceLeft() < 1)
                        return ItemStack.EMPTY;
                  backpackInventory.insertItem(clickedStack);
                  clickedSlot.set(clickedStack);
            } else { // HANDLES INSERT TO INVENTORY
                  clickedStack = backpackInventory.getItemStacks().get(slotId - FIRST_SLOT_INDEX);
                  this.moveItemStackTo(clickedStack, 0, FIRST_SLOT_INDEX, true);
                  if (clickedStack.isEmpty()) backpackInventory.getItemStacks().remove(slotId - FIRST_SLOT_INDEX);
            }
            return ItemStack.EMPTY;
      }

      @Override
      public boolean stillValid(Player player) {
            return true;
      }

      public void removed(Player player) {
            backpackInventory.stopOpen(player);
            mirror.discard();
            super.removed(player);
      }
}
