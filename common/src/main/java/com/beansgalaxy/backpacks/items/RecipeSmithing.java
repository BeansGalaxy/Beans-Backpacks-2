package com.beansgalaxy.backpacks.items;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.Traits;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class RecipeSmithing implements SmithingRecipe {
      public static final String ID = "backpack_smithing";
      public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, ID);
      public static final RecipeSerializer<RecipeSmithing> INSTANCE = new Serializer();
      private final String key;
      private final Traits traits;

      public RecipeSmithing(String key) {
            this.key = key;
            this.traits = Traits.get(this.key);
      }

      @Override
      public boolean isTemplateIngredient(ItemStack var1) {
            return var1.is(traits.template);
      }

      @Override
      public boolean isBaseIngredient(ItemStack var1) {
            return var1.is(traits.base);
      }

      @Override
      public boolean isAdditionIngredient(ItemStack var1) {
            return var1.is(traits.material);
      }

      @Override
      public boolean matches(Container container, Level var2) {
            return container.getItem(0).is(traits.template) &&
                  container.getItem(1).is(traits.base) &&
                  container.getItem(2).is(traits.material);
      }

      @Override
      public boolean isIncomplete() {
            return key == null || key.isEmpty() || traits == null;
      }

      @Override
      public ItemStack assemble(Container var1, RegistryAccess var2) {
            return BackpackItem.stackFromKey(key);
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
            return BackpackItem.stackFromKey(key);
      }

      @Override
      public RecipeSerializer<?> getSerializer() {
            return INSTANCE;
      }

      public static class Serializer implements RecipeSerializer<RecipeSmithing> {
            public static final Codec<RecipeSmithing> CODEC = RecordCodecBuilder.create(
                        in -> in.group(
                                    PrimitiveCodec.STRING.fieldOf("key").forGetter(RecipeSmithing::getKey)
                                          ).apply(in, RecipeSmithing::new)
            );

            @Override
            public Codec<RecipeSmithing> codec() {
                  return CODEC;
            }

            @Override
            public RecipeSmithing fromNetwork(FriendlyByteBuf buf) {
                  String key = buf.readUtf();
                  return new RecipeSmithing(key);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, RecipeSmithing var2) {
                  buf.writeUtf(var2.getKey());
            }
      }

      private String getKey() {
            return this.key;
      }
}
