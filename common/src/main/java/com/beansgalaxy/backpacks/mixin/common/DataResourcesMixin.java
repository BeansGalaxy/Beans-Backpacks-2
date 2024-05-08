package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.beansgalaxy.backpacks.Constants.*;

@Mixin(ReloadableServerResources.class)
public class DataResourcesMixin {

      @Inject(method = "loadResources", at = @At("HEAD"))
      private static void catchDataPacks(ResourceManager resourceManager, RegistryAccess.Frozen frozen, FeatureFlagSet flagSet, Commands.CommandSelection commandSelection, int $$4, Executor $$5, Executor $$6, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
            LOG.info("Reading Data for " + MOD_ID);

            CHESTPLATE_DISABLED.clear();
            DISABLES_BACK_SLOT.clear();
            BLACKLIST_ITEMS.clear();

            NonNullList<Item> blacklistItems = readItemList(resourceManager, "blacklist_items");
            addToList(BLACKLIST_ITEMS, blacklistItems);

            NonNullList<Item> disableChestplate = readItemList(resourceManager, "disable_chestplate");
            addToList(CHESTPLATE_DISABLED, disableChestplate);

            NonNullList<Item> disablesBackSlot = readItemList(resourceManager, "disables_back_slot");
            addToList(DISABLES_BACK_SLOT, disablesBackSlot);

            NonNullList<Item> elytraItems = readItemList(resourceManager, "elytra_items");
            addToList(ELYTRA_ITEMS, elytraItems);

            CHESTPLATE_DISABLED.removeAll(DISABLES_BACK_SLOT);

            Traits.clear();
            resourceManager.listResources("recipes/backpacks", (in) ->
                  in.getPath().endsWith(".json") && in.getNamespace().equals(MOD_ID))
                        .forEach(((resourceLocation, resource) -> {
                  try {
                        BufferedReader bufferedReader = resource.openAsReader();
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null)
                              sb.append(line);
                        bufferedReader.close();
                        String string = sb.toString();

                        JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
                        String key = GsonHelper.getAsString(jsonObject, "backpack_id").toLowerCase();
                        Traits ironTraits = Traits.METAL;

                        JsonObject settings = GsonHelper.getAsJsonObject(jsonObject, "traits");
                        String fallbackName = GsonHelper.getAsString(settings, "fallback_name", ironTraits.name);
                        int maxStacks = GsonHelper.getAsInt(settings, "max_stacks", ServerSave.CONFIG.metal_max_stacks.get());
                        boolean fireResistant = GsonHelper.getAsBoolean(settings, "fire_resistant", ironTraits.fireResistant);
                        String button = GsonHelper.getAsString(settings, "button", ironTraits.button);
                        String material = GsonHelper.getAsString(settings, "material", null);

                        Traits.register(key, new Traits(fallbackName, fireResistant, button, material, maxStacks));
                  } catch (IOException e) {
                        throw new RuntimeException(e);
                  }
            }));

            Traits.register("null", new Traits("Null Backpack", true, "none", null, 11));

            for (Kind kind : Kind.values()) {
                  Traits.register(kind.name(), kind.defaultTraits);
            }
      }
}
