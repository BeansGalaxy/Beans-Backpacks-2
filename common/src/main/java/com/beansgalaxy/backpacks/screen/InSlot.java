package com.beansgalaxy.backpacks.screen;

import com.beansgalaxy.backpacks.access.BucketsAccess;
import com.beansgalaxy.backpacks.data.BackData;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InSlot extends Slot {
      public static final ResourceLocation BACKPACK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
      public static final ResourceLocation INPUT_ALT = new ResourceLocation("sprites/empty_slot_input_alt");
      public final BackData backData;

      public InSlot(BackData backData) {
            super(backData.getBackpackInventory(), 0, ServerSave.CONFIG.back_slot_pos.get(0), ServerSave.CONFIG.back_slot_pos.get(1));
            this.backData = backData;
      }

      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(BACKPACK_ATLAS, INPUT_ALT);
      }

      @Override
      public boolean isActive() {
            Traits.LocalData traits = backData.getTraits();
            boolean storage = traits.isStorage();
            boolean empty = backData.getBackpackInventory().isEmpty();
            return storage || !empty || Kind.is(traits.kind, Kind.POT, Kind.CAULDRON);
      }

      @Override
      public ItemStack getItem() {
            CompoundTag backTag = backData.getStack().getTagElement("back_slot");
            if (backTag == null || !backData.owner.level().isClientSide())
                  return super.getItem();

            String id = backTag.getString("id");
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
            ItemStack stack = item.getDefaultInstance();
            int amount = backTag.getInt("amount");
            if (item instanceof BucketsAccess access) amount /= access.scale();
            stack.setCount(amount);
            return stack;
      }

      public boolean mayPlace(ItemStack stack) {
            return backData.getBackpackInventory().canPlaceItem(stack);
      }
}
