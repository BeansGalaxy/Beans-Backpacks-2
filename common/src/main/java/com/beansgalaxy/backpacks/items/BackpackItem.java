package com.beansgalaxy.backpacks.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class BackpackItem extends Item {
      public final static int DEFAULT_COLOR = 10511680;

      public BackpackItem() {
            super(new Item.Properties().stacksTo(1));
      }

      @Override
      public Component getName(ItemStack stack) {
            return Component.translatable(stack.getOrCreateTagElement("display").getString("name"));
      }

      public void verifyTagAfterLoad(CompoundTag tag) {
            CompoundTag display = tag.getCompound("display");
            String key = display.getString("key");
            String name = display.getString("name");
      }

      @Override
      public InteractionResult useOn(UseOnContext ctx) {
            String key = ctx.getItemInHand().getOrCreateTagElement("display").getString("key");
            System.out.println(key);
            return InteractionResult.PASS;
      }
}
