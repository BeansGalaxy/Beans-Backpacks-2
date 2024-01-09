package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.Kind;
import com.beansgalaxy.backpacks.core.Traits;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class RecipeSmithing implements SmithingRecipe {
      public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, RecipeSmithing.Serializer.ID);

      final Item template;
      final Item material;
      final Item base;
      final String name;
      final String key;
      final String kind;
      final int maxStacks;

      public RecipeSmithing(Item template, Item material, Item base, String name, String key, String kind, int maxStacks) {
            this.template = template;
            this.material = material;
            this.base = base;
            this.name = name;
            this.key = key;
            this.kind = kind;
            this.maxStacks = maxStacks;
      }

      public RecipeSmithing(String key) {
            Traits traits = Traits.get(key);
            this.template = traits.template;
            this.material = traits.material;
            this.base = traits.base;
            this.name = traits.name;
            this.key = key;
            this.kind = traits.kind.name();
            this.maxStacks = traits.maxStacks;
      }


      @Override
      public boolean isTemplateIngredient(ItemStack var1) {
            return var1.is(template);
      }

      @Override
      public boolean isBaseIngredient(ItemStack var1) {
            return var1.is(base);
      }

      @Override
      public boolean isAdditionIngredient(ItemStack var1) {
            return var1.is(material);
      }

      @Override
      public boolean matches(Container container, Level var2) {
            return container.getItem(0).is(template) &&
                  container.getItem(1).is(base) &&
                  container.getItem(2).is(material);
      }

      @Override
      public ItemStack assemble(Container var1, RegistryAccess var2) {
            ItemStack backpackStack = Kind.fromName(kind).getItem().getDefaultInstance();
            CompoundTag tag = var1.getItem(1).getTag().copy();
            backpackStack.setTag(tag);
            CompoundTag display = (CompoundTag) tag.get("display");
            display.putString("key", key);

            return backpackStack;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
            return ItemStack.EMPTY;
      }

      @Override
      public ResourceLocation getId() {
            return ID;
      }

      @Override
      public RecipeSerializer<?> getSerializer() {
            return RecipeSmithing.Serializer.INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<RecipeSmithing> {
            public static final RecipeSmithing.Serializer INSTANCE = new RecipeSmithing.Serializer();
            public static final String ID = "smithing";

            @Override
            public RecipeSmithing fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
                  String key = GsonHelper.getAsString(jsonObject, "key", "");
                  return new RecipeSmithing(key);
            }

            @Override
            public RecipeSmithing fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
                  Item template = buf.readItem().getItem();
                  Item material = buf.readItem().getItem();
                  Item base = buf.readItem().getItem();
                  String name = buf.readUtf();
                  String kind = buf.readUtf();
                  String key = buf.readUtf();
                  int maxStacks = buf.readInt();

                  return new RecipeSmithing(template, material, base, name, kind, key, maxStacks);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, RecipeSmithing var2) {
                  buf.writeItem(var2.getTemplate().getDefaultInstance());
                  buf.writeItem(var2.getMaterial().getDefaultInstance());
                  buf.writeItem(var2.getBase().getDefaultInstance());
                  buf.writeUtf(var2.getName());
                  buf.writeUtf(var2.getKind());
                  buf.writeUtf(var2.getKey());
                  buf.writeInt(var2.getMaxStacks());
            }
      }

      private Integer getMaxStacks() {
            return this.maxStacks;
      }

      private String getKind() {
            return this.kind;
      }

      private String getKey() {
            return this.key;
      }

      private String getName() {
            return this.name;
      }

      private Item getBase() {
            return base;
      }

      private Item getMaterial() {
            return material;
      }

      private Item getTemplate() {
            return template;
      }
}
