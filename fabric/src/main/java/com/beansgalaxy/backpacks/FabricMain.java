package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.compat.TrinketsRegistry;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.entity.EntityFlight;
import com.beansgalaxy.backpacks.entity.EntityGeneral;
import com.beansgalaxy.backpacks.events.*;
import com.beansgalaxy.backpacks.events.advancements.EquipAnyCriterion;
import com.beansgalaxy.backpacks.events.advancements.PlaceCriterion;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.inventory.BackpackInventory;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.EnderBackpack;
import com.beansgalaxy.backpacks.items.WingedBackpack;
import com.beansgalaxy.backpacks.items.recipes.Conversion;
import com.beansgalaxy.backpacks.items.recipes.Crafting;
import com.beansgalaxy.backpacks.items.recipes.Smithing;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.HashMap;

public class FabricMain implements ModInitializer {
    public static EquipAnyCriterion EQUIP_ANY = CriteriaTriggers.register(new EquipAnyCriterion());
    public static PlaceCriterion PLACE = CriteriaTriggers.register(new PlaceCriterion());
    public static SpecialCriterion SPECIAL = CriteriaTriggers.register(new SpecialCriterion());
    public static final ResourceLocation INITIAL_SYNC = new ResourceLocation(Constants.MOD_ID, "initial_sync");
    public static final HashMap<String, SoundEvent> SOUNDS = new HashMap<>();
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        NetworkPackages.register2S();

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(new SyncDataEvent());
        ServerLifecycleEvents.SERVER_STARTED.register(new ServerLifecycleEvent());
        ServerLifecycleEvents.SERVER_STOPPING.register(new ServerLifecycleEvent());
        ServerPlayerEvents.COPY_FROM.register(new CopyPlayerEvent());
        ServerEntityEvents.ENTITY_LOAD.register(new LoadEntityEvent());
        EntityElytraEvents.CUSTOM.register(new ElytraFlightEvent());
        UseBlockCallback.EVENT.register(new PlayerInteractEvent());
        registerSounds();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                    RegisterCommands.register(dispatcher));

        if (Services.COMPAT.isModLoaded(CompatHelper.TRINKETS))
            TrinketsRegistry.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EnderStorage.get(handler.player.level()).MAP.forEach(((uuid, enderData) -> {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeUUID(uuid);
                buf.writeNbt(enderData.getTrim());

                CompoundTag tag = new CompoundTag();
                BackpackInventory.writeNbt(tag, enderData.getItemStacks());
                buf.writeNbt(tag);
                String string = Component.Serializer.toJson(enderData.getPlayerName());
                buf.writeUtf(string);

                server.execute(() -> ServerPlayNetworking.send(handler.getPlayer(), INITIAL_SYNC, buf));
            }));
        });

        Constants.LOG.info("Initializing Beans' Backpacks Fabric");
    }

    /* ========================================= ITEM REGISTRY ========================================= */
    public static final Item LEATHER_BACKPACK = registerItem("backpack", new DyableBackpack(new Item.Properties().stacksTo(1)));
    public static final Item METAL_BACKPACK = registerItem("metal_backpack", new BackpackItem(new Item.Properties().stacksTo(1)));
    public static final Item UPGRADED_BACKPACK = registerItem("upgraded_backpack", new BackpackItem(new Item.Properties().fireResistant().stacksTo(1)));
    public static final Item WINGED_BACKPACK = registerItem("winged_backpack", new WingedBackpack(new Item.Properties().defaultDurability(432).rarity(Rarity.UNCOMMON)));
    public static final Item ENDER_BACKPACK = registerItem("ender_backpack", new EnderBackpack(new Item.Properties().stacksTo(1)));
    public static final Item BACK_BUNDLE = registerItem("back_bundle", new DyableBackpack(new Item.Properties().stacksTo(1)));

    private static Item registerItem(String name, Item item)
    {
        ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

    /* ========================================= REGISTER RECIPES ========================================= */
    public static final RecipeSerializer<Crafting> RECIPE_CRAFTING = Registry.register(
                            BuiltInRegistries.RECIPE_SERIALIZER, Crafting.LOCATION, Crafting.INSTANCE);

    public static final RecipeSerializer<Smithing> RECIPE_SMITHING = Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER, Smithing.LOCATION, Smithing.INSTANCE);

    public static final RecipeSerializer<Conversion> RECIPE_CONVERSION = Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER, Conversion.LOCATION, Conversion.INSTANCE);

    /* ========================================= REGISTER CREATIVE TAB ========================================= */
    public static final CreativeModeTab BACKPACK_TAB = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                .icon(() -> new ItemStack(LEATHER_BACKPACK))
                .displayItems(Constants.CREATIVE_TAB_GENERATOR).build();

    public static final CreativeModeTab CREATIVE_TAB =
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(Constants.MOD_ID, "backpacks"), BACKPACK_TAB);

    /* ========================================= REGISTER ENTITY ========================================= */
    public static final EntityType<Entity> BACKPACK_ENTITY =
                Registry.register(BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Constants.MOD_ID, "backpack"),
                FabricEntityTypeBuilder.create(MobCategory.MISC, EntityGeneral::new).build());

    public static final EntityType<Entity> ENDER_ENTITY =
                Registry.register(BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Constants.MOD_ID, "ender_backpack"),
                FabricEntityTypeBuilder.create(MobCategory.MISC, EntityEnder::new).build());

    public static final EntityType<Entity> WINGED_ENTITY =
                Registry.register(BuiltInRegistries.ENTITY_TYPE,
                            new ResourceLocation(Constants.MOD_ID, "winged_backpack"),
                            FabricEntityTypeBuilder.create(MobCategory.MISC, EntityFlight::new).build());

    /* ========================================= REGISTER MISC ========================================= */
    public static final MenuType<BackpackMenu> BACKPACK_MENU =
                Registry.register(BuiltInRegistries.MENU,
                new ResourceLocation(Constants.MOD_ID, "backpack_menu"),
                new ExtendedScreenHandlerType<>(BackpackMenu::new));

    public static void registerSounds() {
        for (PlaySound.Events value : PlaySound.Events.values()) {
            String id = value.id;
            ResourceLocation location = new ResourceLocation(Constants.MOD_ID, id);
            SoundEvent event = SoundEvent.createVariableRangeEvent(location);
            SoundEvent register = Registry.register(BuiltInRegistries.SOUND_EVENT, location, event);
            SOUNDS.put(id, register);
        }
    }
}
