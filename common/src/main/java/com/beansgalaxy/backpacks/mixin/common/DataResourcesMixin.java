package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.Traits;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.flag.FeatureFlagSet;
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
            Constants.LOG.info("Reading Data for " + MOD_ID);

            Constants.CHESTPLATE_DISABLED.clear();
            Constants.DISABLES_BACK_SLOT.clear();
            Constants.BLACKLIST_ITEMS.clear();

            Constants.addToList(Constants.BLACKLIST_ITEMS,
                    readItemList(resourceManager, "blacklist_items"));

            Constants.addToList(Constants.ELYTRA_ITEMS,
                    readItemList(resourceManager, "elytra_items"));

            Constants.addToList(Constants.CHESTPLATE_DISABLED,
                        readItemList(resourceManager, "disable_chestplate"));

            Constants.addToList(Constants.DISABLES_BACK_SLOT,
                        readItemList(resourceManager, "disables_back_slot"));

            Constants.CHESTPLATE_DISABLED.removeAll(Constants.DISABLES_BACK_SLOT);

            HashSet<String> removedKeys =
                        new HashSet<>(readStringList(resourceManager, "remove_backpack_keys"));

            Traits.clear();
            resourceManager.listResources("recipes/backpacks", (in) ->
                  in.getPath().endsWith(".json") && in.getNamespace().equals(Constants.MOD_ID))
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

                        String key = GsonHelper.getAsString(jsonObject, "backpack_id");
                        if (!removedKeys.contains(key)) {
                              JsonObject settings = GsonHelper.getAsJsonObject(jsonObject, "settings");
                              String fallbackName = GsonHelper.getAsString(settings, "fallback_name", "Iron Backpack");
                              int maxStacks = GsonHelper.getAsInt(settings, "max_stacks", 7);
                              boolean fireResistant = GsonHelper.getAsBoolean(settings, "fire_resistant", false);
                              String button_material = GsonHelper.getAsString(settings, "button_material", "diamond");
                              Traits.Button button = Traits.Button.fromName(button_material);

                              Traits.register(key, new Traits(fallbackName, maxStacks, fireResistant, button));
                        }

                  } catch (IOException e) {
                        throw new RuntimeException(e);
                  }
            }));
      }
}
