package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Smithing implements SmithingRecipe {
      public static final String ID = "smithing";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<Smithing> INSTANCE = new Smithing.Serializer();
      private final String backpack_id;
      private final Ingredient template;
      private final Ingredient base;
      private final Ingredient addition;

      public Smithing(String backpack_id, Ingredient template, Ingredient base, Ingredient addition) {
            this.backpack_id = backpack_id;
            this.template = template;
            this.base = base;
            this.addition = addition;
      }

      @Override
      public boolean isTemplateIngredient(ItemStack itemStack) {
            return template.test(itemStack);
      }

      @Override
      public boolean isBaseIngredient(ItemStack itemStack) {
            return base.test(itemStack);
      }

      @Override
      public boolean isAdditionIngredient(ItemStack itemStack) {
            return addition.test(itemStack);
      }

      @Override
      public boolean matches(Container container, Level level) {
            if (container.getContainerSize() < 3)
                  return false;

            return template.test(container.getItem(0)) && base.test(container.getItem(1)) && addition.test(container.getItem(2));
      }

      @Override @NotNull
      public ItemStack assemble(Container container, RegistryAccess registryAccess) {
            ItemStack stack = Traits.toStack(backpack_id);
            if (container.getContainerSize() > 2) {
                  ItemStack base = container.getItem(1);
                  CompoundTag trim = base.getTagElement("Trim");
                  if (trim != null)
                        stack.getOrCreateTag().put("Trim", trim);
            }
            return stack;
      }

      @Override @NotNull
      public ItemStack getResultItem(RegistryAccess registryAccess) {
            return Traits.toStack(backpack_id);
      }

      @Override @NotNull
      public ResourceLocation getId() {
            return LOCATION.withSuffix("_" + backpack_id);
      }

      @Override @NotNull
      public RecipeSerializer<?> getSerializer() {
            return INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<Smithing> {

            @Override @NotNull
            public Smithing fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
                  String key = GsonHelper.getAsString(jsonObject, "backpack_id", "");
                  Ingredient template = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "template"));
                  Ingredient base = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "base"));
                  Ingredient addition = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "addition"));
                  return new Smithing(key, template, base, addition);
            }

            @Override @NotNull
            public Smithing fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
                  String key = buf.readUtf();
                  Ingredient template = Ingredient.fromNetwork(buf);
                  Ingredient base = Ingredient.fromNetwork(buf);
                  Ingredient addition = Ingredient.fromNetwork(buf);
                  return new Smithing(key, template, base, addition);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, Smithing recipe) {
                  buf.writeUtf(recipe.backpack_id);
                  recipe.template.toNetwork(buf);
                  recipe.base.toNetwork(buf);
                  recipe.addition.toNetwork(buf);
            }
      }
}
