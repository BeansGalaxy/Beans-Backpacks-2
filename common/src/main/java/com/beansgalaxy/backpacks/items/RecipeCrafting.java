package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeCrafting extends CustomRecipe {
      final Item material;
      final Item binder;
      final String name;
      final String kind;
      final String key;
      final int maxStacks;

      public RecipeCrafting(Item material, Item binder, String name, String kind, String key, int maxStacks) {
            super(CraftingBookCategory.EQUIPMENT);
            this.key = key;
            this.material = material;
            this.binder = binder;
            this.name = name;
            this.kind = kind;
            this.maxStacks = maxStacks;
      }

      public RecipeCrafting(String key) {
            super(CraftingBookCategory.EQUIPMENT);
            Traits traits = Traits.get(key);
            this.key = key;
            this.material = traits.material;
            this.binder = traits.binder;
            this.name = traits.name;
            this.kind = traits.kind.name();
            this.maxStacks = traits.maxStacks;
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

            CompoundTag display = new CompoundTag();
            display.putString("key", key);
            stack.getOrCreateTag().put("display", display);
            return stack;
      }

      @Override
      public boolean canCraftInDimensions(int var1, int var2) {
            return false;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
            return BackpackItem.stackFromKey(key);
      }

      @Override
      public NonNullList<Ingredient> getIngredients() {
            Ingredient mat = Ingredient.of(material);
            Ingredient bin = Ingredient.of(binder);
            Ingredient emp = Ingredient.EMPTY;

            return NonNullList.of(
                  mat, bin, mat,
                  mat, emp, mat,
                  mat, bin, mat
            );
      }

      @Override
      public RecipeSerializer<?> getSerializer() {
            return Serializer.INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<RecipeCrafting> {
            public static final Serializer INSTANCE = new Serializer();
            public static final String ID = "crafting";

            public static final Codec<RecipeCrafting> CODEC = RecordCodecBuilder.create(
                        in -> in.group(
                                                PrimitiveCodec.STRING.fieldOf("key").forGetter(RecipeCrafting::getKey)
                                    )
                                    .apply(in, RecipeCrafting::new)
            );

            @Override
            public Codec<RecipeCrafting> codec() {
                  return CODEC;
            }

            @Override
            public RecipeCrafting fromNetwork(FriendlyByteBuf buf) {
                  Item material = buf.readItem().getItem();
                  Item binder = buf.readItem().getItem();
                  String name = buf.readUtf();
                  String kind = buf.readUtf();
                  String key = buf.readUtf();
                  int maxStacks = buf.readInt();

                  return new RecipeCrafting(material, binder, name, kind, key, maxStacks);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, RecipeCrafting var2) {
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

      public String getKey() {
            return key;
      }

      private int getMaxStacks() {
            return maxStacks;
      }
}
