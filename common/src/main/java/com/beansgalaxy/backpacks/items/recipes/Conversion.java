package com.beansgalaxy.backpacks.items.recipes;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Conversion extends CustomRecipe {
      public static final String ID = "conversion";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<Conversion> INSTANCE = new Conversion.Serializer();
      private final String old_backpack_id;
      private final String new_backpack_id;

      public Conversion(ResourceLocation $$0, String oldBackpackId, String newBackpackId) {
            super($$0, CraftingBookCategory.MISC);
            old_backpack_id = oldBackpackId;
            new_backpack_id = newBackpackId;
      }

      @Override
      public boolean matches(CraftingContainer craftingContainer, Level level) {
            int containerSize = craftingContainer.getContainerSize();
            int empties = 1;
            for (int i = 0; i < containerSize; i++) {
                  ItemStack item = craftingContainer.getItem(i);
                  if (item.isEmpty()) {
                        empties++;
                        continue;
                  }
                  CompoundTag tag = item.getTag();
                  if (tag == null || !tag.getString("backpack_id").equals(old_backpack_id))
                        return false;
            }
            return empties == containerSize;
      }

      @Override @NotNull
      public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
            ItemStack stack = ItemStack.EMPTY;
            int containerSize = craftingContainer.getContainerSize();
            while (stack.isEmpty() && containerSize >= 0) {
                  containerSize--;
                  ItemStack item = craftingContainer.getItem(containerSize);
                  if (item.isEmpty()) continue;

                  CompoundTag tag = item.copy().getOrCreateTag();
                  tag.putString("backpack_id", new_backpack_id);
                  ItemStack netherite = Services.REGISTRY.getMetal().getDefaultInstance();
                  netherite.setTag(tag);
                  stack = netherite;
            }

            return stack;
      }

      @Override
      public boolean canCraftInDimensions(int i, int i1) {
            return true;
      }

      @Override
      public boolean isIncomplete() {
            return Constants.isEmpty(old_backpack_id) || Constants.isEmpty(new_backpack_id);
      }

      @Override @NotNull
      public ItemStack getResultItem(RegistryAccess registryAccess) {
            return Traits.toStack(new_backpack_id);
      }

      @Override
      public NonNullList<Ingredient> getIngredients() {
            return NonNullList.of(Ingredient.of(Services.REGISTRY.getMetal()));
      }

      @Override @NotNull
      public RecipeSerializer<?> getSerializer() {
            return INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<Conversion> {

            @Override @NotNull
            public Conversion fromJson(ResourceLocation id, JsonObject jsonObject) {
                  String oldBackpackId = GsonHelper.getAsString(jsonObject, "old_backpack_id");
                  String newBackpackId = GsonHelper.getAsString(jsonObject, "new_backpack_id");
                  return new Conversion(id, oldBackpackId, newBackpackId);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, Conversion recipe) {
                  buf.writeUtf(recipe.old_backpack_id);
                  buf.writeUtf(recipe.new_backpack_id);
            }

            @Override @NotNull
            public Conversion fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                  String oldBackpackID = buf.readUtf();
                  String newBackpackID = buf.readUtf();
                  return new Conversion(id, oldBackpackID, newBackpackID);
            }
      }
}
