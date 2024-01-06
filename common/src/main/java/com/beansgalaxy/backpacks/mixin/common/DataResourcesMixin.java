package com.beansgalaxy.backpacks.mixin.common;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.entity.MobileData;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class DataResourcesMixin {

      @Inject(method = "loadResources", at = @At("HEAD"))
      private static void catchDataPacks(ResourceManager resourceManager, RegistryAccess.Frozen frozen, FeatureFlagSet flagSet, Commands.CommandSelection commandSelection, int $$4, Executor $$5, Executor $$6, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
            Map<ResourceLocation, Resource> disableChestplate = resourceManager.listResources("modify",
                        (in) -> in.getPath().endsWith("disable_chestplate"));

            disableChestplate.forEach( (resourceLocation, resource) -> {
                  try {
                        InputStream open = resource.open();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(open));

                        String line;
                        while ((line = reader.readLine()) != null) {
                              String[] split = line.replaceAll(" ", "").split(",");
                              for (String id: split) {
                                    Constants.disableFromChestplate(id);
                              }
                        }
                  } catch (IOException e) {
                        throw new RuntimeException(e);
                  }
            });

            Map<ResourceLocation, Resource> disablesBackSlot = resourceManager.listResources("modify",
                        (in) -> in.getPath().endsWith("disables_back_slot"));

            disablesBackSlot.forEach( (resourceLocation, resource) -> {
                  try {
                        InputStream open = resource.open();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(open));

                        String line;
                        while ((line = reader.readLine()) != null) {
                              String[] split = line.replaceAll(" ", "").split(",");
                              for (String id: split) {
                                    Constants.disablesBackSlot(id);
                              }
                        }
                  } catch (IOException e) {
                        throw new RuntimeException(e);
                  }
            });

            Map<ResourceLocation, Resource> recipeKinds = resourceManager.listResources("recipes",
                        (in) -> in.getPath().endsWith(".json") && in.getNamespace().equals(Constants.MOD_ID));

            recipeKinds.forEach(((resourceLocation, resource) -> {
                  try {
                        InputStream open = resource.open();
                        String json = IOUtils.toString(open, StandardCharsets.UTF_8);

                        ObjectMapper map = ObjectMapper.create();
                        MobileData.Raw data = map.readValue(json, MobileData.Raw.class);

                        Kind kind = Kind.fromName(data.kind);

                        Constants.REGISTERED_DATA.put(data.key, new MobileData(data.key, data.name, kind, data.max_stacks));

                  } catch (IOException e) {
                        throw new RuntimeException(e);
                  }
            }));
      }
}
