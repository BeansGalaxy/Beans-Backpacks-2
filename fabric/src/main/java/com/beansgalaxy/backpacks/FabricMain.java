package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.ElytraFlightEvent;
import com.beansgalaxy.backpacks.events.PlayerInteractEvent;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.RecipeCrafting;
import com.beansgalaxy.backpacks.items.RecipieSmithing;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class FabricMain implements ModInitializer {
    
    @Override
    public void onInitialize() {
        NetworkPackages.registerC2SPackets();

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constants.MOD_ID, RecipeCrafting.Serializer.ID),
                    RecipeCrafting.Serializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constants.MOD_ID, RecipieSmithing.Serializer.ID),
                    RecipieSmithing.Serializer.INSTANCE);

        EntityElytraEvents.CUSTOM.register(new ElytraFlightEvent());
        UseBlockCallback.EVENT.register(new PlayerInteractEvent());
        Constants.LOG.info("Initializing Beans' Backpacks Fabric");
        CommonClass.init();
    }

    // REGISTERS ENTITY
    public static final EntityType<Entity> ENTITY = Registry.register(
                BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Constants.MOD_ID, "backpack"),
                FabricEntityTypeBuilder.create(MobCategory.MISC, BackpackEntity::new).build());

    // REGISTERS MENUS
    public static final MenuType<BackpackMenu> BACKPACK_MENU = Registry.register(
                BuiltInRegistries.MENU, new ResourceLocation(Constants.MOD_ID, "backpack_menu"),
                new ExtendedScreenHandlerType<>(BackpackMenu::new));

    // REGISTERS ITEMS
    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(FabricMain::addItemsToTab);
    }

    private static void addItemsToTab(FabricItemGroupEntries entries) {
        entries.accept(LEATHER_BACKPACK);
        entries.accept(METAL_BACKPACK);
        entries.accept(UPGRADED_BACKPACK);
    }

    public static final Item LEATHER_BACKPACK = registerItem("backpack", new DyableBackpack());
    public static final Item METAL_BACKPACK = registerItem("metal_backpack", new BackpackItem());
    public static final Item UPGRADED_BACKPACK = registerItem("upgraded_backpack", new BackpackItem());

    private static Item registerItem(String name, Item item) {
        ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

}
