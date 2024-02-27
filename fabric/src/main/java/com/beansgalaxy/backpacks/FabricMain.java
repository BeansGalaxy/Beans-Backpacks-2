package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.core.BackpackInventory;
import com.beansgalaxy.backpacks.entity.*;
import com.beansgalaxy.backpacks.events.*;
import com.beansgalaxy.backpacks.events.advancements.EquipAnyCriterion;
import com.beansgalaxy.backpacks.events.advancements.PlaceCriterion;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.*;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FabricMain implements ModInitializer {
    public static EquipAnyCriterion EQUIP_ANY = CriteriaTriggers.register(new EquipAnyCriterion());
    public static PlaceCriterion PLACE = CriteriaTriggers.register(new PlaceCriterion());
    public static SpecialCriterion SPECIAL = CriteriaTriggers.register(new SpecialCriterion());
    public static final ResourceLocation INITIAL_SYNC = new ResourceLocation(Constants.MOD_ID, "initial_sync");
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        NetworkPackages.registerC2SPackets();

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(new SyncDataEvent());
        ServerLifecycleEvents.SERVER_STARTED.register(new ServerStartEvent());
        ServerPlayerEvents.COPY_FROM.register(new CopyPlayerEvent());
        ServerLivingEntityEvents.AFTER_DEATH.register(new LivingEntityDeath());
        EntityElytraEvents.CUSTOM.register(new ElytraFlightEvent());
        UseBlockCallback.EVENT.register(new PlayerInteractEvent());
        Sounds.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerSave.MAPPED_ENDER_DATA.forEach(((uuid, enderData) -> {
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

    public static final Item LEATHER_BACKPACK = registerItem("backpack", new DyableBackpack(new Item.Properties().stacksTo(1)));
    public static final Item METAL_BACKPACK = registerItem("metal_backpack", new BackpackItem(new Item.Properties().stacksTo(1)));
    public static final Item UPGRADED_BACKPACK = registerItem("upgraded_backpack", new BackpackItem(new Item.Properties().fireResistant().stacksTo(1)));
    public static final Item WINGED_BACKPACK = registerItem("winged_backpack", new WingedBackpack(new Item.Properties().defaultDurability(432).rarity(Rarity.UNCOMMON)));
    public static final Item ENDER_BACKPACK = registerItem("ender_backpack", new EnderBackpack(new Item.Properties().stacksTo(1)));

    private static Item registerItem(String name, Item item)
    {
        ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

    public static final RecipeSerializer<RecipeCrafting> RECIPE_CRAFTING = Registry.register(
                            BuiltInRegistries.RECIPE_SERIALIZER, RecipeCrafting.LOCATION, RecipeCrafting.INSTANCE);

    public static final RecipeSerializer<RecipeSmithing> RECIPE_SMITHING = Registry.register(
                            BuiltInRegistries.RECIPE_SERIALIZER, RecipeSmithing.LOCATION, RecipeSmithing.INSTANCE);

    // REGISTER CREATIVE TAB
    public static final CreativeModeTab BACKPACK_TAB = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                .icon(() -> new ItemStack(LEATHER_BACKPACK))
                .displayItems((params, output) -> {
                    Constants.TRAITS_MAP.keySet().forEach(key ->
                                output.accept(BackpackItem.stackFromKey(key)));
                    output.accept(Items.DECORATED_POT);
                }).build();

    public static final CreativeModeTab CREATIVE_TAB =
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(Constants.MOD_ID, "backpacks"), BACKPACK_TAB);

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
                            FabricEntityTypeBuilder.create(MobCategory.MISC, EntityWinged::new).build());

    public static final MenuType<BackpackMenu> BACKPACK_MENU =
                Registry.register(BuiltInRegistries.MENU,
                new ResourceLocation(Constants.MOD_ID, "backpack_menu"),
                new ExtendedScreenHandlerType<>(BackpackMenu::new));

}
