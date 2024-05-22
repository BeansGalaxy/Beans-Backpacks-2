package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.compat.CurioRegistry;
import com.beansgalaxy.backpacks.entity.EntityEnder;
import com.beansgalaxy.backpacks.entity.EntityFlight;
import com.beansgalaxy.backpacks.entity.EntityGeneral;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.events.advancements.EquipAnyCriterion;
import com.beansgalaxy.backpacks.events.advancements.PlaceCriterion;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.*;
import com.beansgalaxy.backpacks.items.recipes.Conversion;
import com.beansgalaxy.backpacks.items.recipes.Crafting;
import com.beansgalaxy.backpacks.items.recipes.Smithing;
import com.beansgalaxy.backpacks.items.recipes.SuperSpecialRecipe;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

@Mod(Constants.MOD_ID)
public class ForgeMain {
    public static EquipAnyCriterion EQUIP_ANY = CriteriaTriggers.register(new EquipAnyCriterion());
    public static PlaceCriterion PLACE = CriteriaTriggers.register(new PlaceCriterion());
    public static SpecialCriterion SPECIAL = CriteriaTriggers.register(new SpecialCriterion());
    public static final HashMap<String, RegistryObject<SoundEvent>> SOUNDS = new HashMap<>();

    public ForgeMain() {
        CommonClass.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(bus);
        RECIPES.register(bus);
        ENTITIES.register(bus);
        MENU_TYPES.register(bus);
        CREATIVE_TABS.register(bus);
        SOUND_EVENTS.register(bus);
        registerSounds();

        Constants.LOG.info("Initializing Beans' Backpacks Forge");

        if (Services.COMPAT.isModLoaded(CompatHelper.CURIOS))
            bus.addListener(CurioRegistry::register);


    }

    // SOUNDS REGISTRY
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
                DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MOD_ID);

    private static void registerSounds() {
        for (PlaySound.Events value : PlaySound.Events.values()) {
            String id = value.id;
            RegistryObject<SoundEvent> register = ForgeMain.SOUND_EVENTS.register(id, () ->
                        SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, id)));

            ForgeMain.SOUNDS.put(id, register);
        }
    }

    // REGISTER MENUS
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
                DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

    public static final RegistryObject<MenuType<BackpackMenu>> MENU = MENU_TYPES.register("backpack_menu",
                () -> IForgeMenuType.create(BackpackMenu::new));

    // REGISTER ENTITIES
    public static final DeferredRegister<EntityType<?>> ENTITIES =
                DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<EntityType<EntityGeneral>> ENTITY_GENERAL = ENTITIES.register("backpack",
                () -> EntityType.Builder.<EntityGeneral>of(EntityGeneral::new, MobCategory.MISC).build(new ResourceLocation(Constants.MOD_ID, "backpack").toString()));

    public static final RegistryObject<EntityType<EntityEnder>> ENTITY_ENDER = ENTITIES.register("ender_backpack",
                () -> EntityType.Builder.<EntityEnder>of(EntityEnder::new, MobCategory.MISC).build(new ResourceLocation(Constants.MOD_ID, "ender_backpack").toString()));

    public static final RegistryObject<EntityType<EntityFlight>> ENTITY_WINGED = ENTITIES.register("winged_backpack",
                () -> EntityType.Builder.<EntityFlight>of(EntityFlight::new, MobCategory.MISC).build(new ResourceLocation(Constants.MOD_ID, "winged_backpack").toString()));

    // REGISTER RECIPES
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<Crafting>> BACKPACK_CRAFTING =
                RECIPES.register(Crafting.ID, () -> Crafting.INSTANCE);

    public static final RegistryObject<RecipeSerializer<Smithing>> BACKPACK_SMITHING =
                RECIPES.register(Smithing.ID, () -> Smithing.INSTANCE);

    public static final RegistryObject<RecipeSerializer<Conversion>> BACKPACK_CONVERSION =
                RECIPES.register(Conversion.ID, () -> Conversion.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SuperSpecialRecipe>> BACKPACK_SUPER_SPECIAL =
                RECIPES.register(SuperSpecialRecipe.ID, () -> SuperSpecialRecipe.INSTANCE);

    // REGISTER ITEMS
    public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack", () ->
                new DyableBackpack(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> METAL_BACKPACK = ITEMS.register("metal_backpack", () ->
                new BackpackItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WINGED_BACKPACK = ITEMS.register("winged_backpack", () ->
                new WingedBackpack(new Item.Properties().defaultDurability(432).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ENDER_BACKPACK = ITEMS.register("ender_backpack", () ->
                new EnderBackpack(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BACK_BUNDLE = ITEMS.register("back_bundle", () ->
                new BackBundle(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LOCK = ITEMS.register("lock", () ->
                new Item(new Item.Properties()));

    // REGISTER CREATIVE TAB
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_TABS.register("backpacks", () ->
                CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                            .icon(() -> new ItemStack(LEATHER_BACKPACK.get()))
                            .displayItems(Constants.CREATIVE_TAB_GENERATOR).build());

}