package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.general.Kind;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BackpackRecipe extends CustomRecipe {
      final Item material;
      final Item binder;
      final String name;
      final String kind;
      final String key;
      final int maxStacks;

      public BackpackRecipe(Item material, Item binder, String name, String kind, String key, int maxStacks) {
            super(CraftingBookCategory.MISC);
            this.material = material;
            this.binder = binder;
            this.name = name;
            this.kind = kind;
            this.key = key;
            this.maxStacks = maxStacks;
      }

      public Item getMaterial() {
            return material;
      }

      public Item getBinder() {
            return binder;
      }

      public String getName() {
            return name;
      }

      @Override
      public boolean matches(CraftingContainer container, Level var2) {
            return container.getItem(1).is(getBinder()) &&
                  container.getItem(7).is(getBinder()) &&
                  container.getItem(0).is(getMaterial()) &&
                  container.getItem(2).is(getMaterial()) &&
                  container.getItem(3).is(getMaterial()) &&
                  container.getItem(5).is(getMaterial()) &&
                  container.getItem(6).is(getMaterial()) &&
                  container.getItem(8).is(getMaterial());
      }

      @Override
      public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
            Kind kind = Kind.fromName(this.kind);
            Item item = kind.getItem();
            ItemStack stack = item.getDefaultInstance();
            BackpackItem backpackItem = (BackpackItem) stack.getItem();

            CompoundTag display = new CompoundTag();
            display.putString("key", key);
            display.putString("name", name);
            display.putInt("max_stacks", maxStacks);
            //display.putInt("color", BackpackItem.DEFAULT_COLOR);
            stack.getOrCreateTag().put("display", display);
            return stack;
      }

      @Override
      public boolean canCraftInDimensions(int var1, int var2) {
            return false;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
            return ItemStack.EMPTY;
      }

      @Override
      public RecipeSerializer<?> getSerializer() {
            return Serializer.INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<BackpackRecipe> {
            public static final Serializer INSTANCE = new Serializer();
            public static final String ID = "crafting";

            public static final Codec<BackpackRecipe> CODEC = RecordCodecBuilder.create(
                        in -> in.group(
                                                BuiltInRegistries.ITEM.byNameCodec().fieldOf("material").forGetter(BackpackRecipe::getMaterial),
                                                BuiltInRegistries.ITEM.byNameCodec().fieldOf("binder").forGetter(BackpackRecipe::getBinder),
                                                PrimitiveCodec.STRING.fieldOf("name").forGetter(BackpackRecipe::getName),
                                                PrimitiveCodec.STRING.fieldOf("kind").forGetter(BackpackRecipe::getKind),
                                                PrimitiveCodec.STRING.fieldOf("key").forGetter(BackpackRecipe::getKey),
                                                PrimitiveCodec.INT.fieldOf("max_stacks").forGetter(BackpackRecipe::getMaxStacks)
                                    )
                                    .apply(in, BackpackRecipe::new)
            );

            @Override
            public Codec<BackpackRecipe> codec() {
                  return CODEC;
            }

            @Override
            public BackpackRecipe fromNetwork(FriendlyByteBuf buf) {
                  Item material = buf.readItem().getItem();
                  Item binder = buf.readItem().getItem();
                  String name = buf.readUtf();
                  String kind = buf.readUtf();
                  String key = buf.readUtf();
                  int maxStacks = buf.readInt();

                  return new BackpackRecipe(material, binder, name, kind, key, maxStacks);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, BackpackRecipe var2) {
                  buf.writeItem(var2.getMaterial().getDefaultInstance());
                  buf.writeItem(var2.getBinder().getDefaultInstance());
                  buf.writeUtf(var2.getName());
                  buf.writeUtf(var2.getKind());
                  buf.writeUtf(var2.getKey());
                  buf.writeInt(var2.getMaxStacks());
            }
      }

      private String getKind() {
            return kind;
      }

      private String getKey() {
            return key;
      }

      private int getMaxStacks() {
            return maxStacks;
      }
}
