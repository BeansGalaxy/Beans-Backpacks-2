package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.entity.BackpackEntity;
import com.beansgalaxy.backpacks.events.advancements.EquipAnyCriterion;
import com.beansgalaxy.backpacks.events.advancements.PlaceCriterion;
import com.beansgalaxy.backpacks.events.advancements.SpecialCriterion;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import com.beansgalaxy.backpacks.items.RecipeCrafting;
import com.beansgalaxy.backpacks.items.RecipeSmithing;
import com.beansgalaxy.backpacks.screen.BackpackMenu;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Constants.MOD_ID)
public class ForgeMain {
    public static EquipAnyCriterion EQUIP_ANY = CriteriaTriggers.register(new EquipAnyCriterion());
    public static PlaceCriterion PLACE = CriteriaTriggers.register(new PlaceCriterion());
    public static SpecialCriterion SPECIAL = CriteriaTriggers.register(new SpecialCriterion());

    public ForgeMain() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(bus);
        RECIPES.register(bus);
        ENTITIES.register(bus);
        MENU_TYPES.register(bus);
        CREATIVE_TABS.register(bus);
        Sounds.register(bus);

        Constants.LOG.info("Initializing Beans' Backpacks Forge");
        CommonClass.init();
    }

    // REGISTER MENUS
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
                DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

    public static final RegistryObject<MenuType<BackpackMenu>> MENU = MENU_TYPES.register("backpack_menu",
                () -> IForgeMenuType.create(BackpackMenu::new));



    // REGISTER ENTITIES
    public static final DeferredRegister<EntityType<?>> ENTITIES =
                DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<EntityType<BackpackEntity>> ENTITY = ENTITIES.register("backpack",
                () -> EntityType.Builder.<BackpackEntity>of(BackpackEntity::new, MobCategory.MISC).build(new ResourceLocation(Constants.MOD_ID, "backpack").toString()));

    // REGISTER RECIPES
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RecipeCrafting>> BACKPACK_RECIPE =
                RECIPES.register(RecipeCrafting.ID, () -> RecipeCrafting.INSTANCE);

    public static final RegistryObject<RecipeSerializer<RecipeSmithing>> BACKPACK_SMITHING_RECIPE =
                RECIPES.register(RecipeSmithing.ID, () -> RecipeSmithing.INSTANCE);

    // REGISTER ITEMS
    public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack", DyableBackpack::new);
    public static final RegistryObject<Item> METAL_BACKPACK = ITEMS.register("metal_backpack", BackpackItem::new);
    public static final RegistryObject<Item> UPGRADED_BACKPACK = ITEMS.register("upgraded_backpack", BackpackItem::new);

    // REGISTER CREATIVE TAB
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_TABS.register("backpacks", () ->
                CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
                            .icon(() -> new ItemStack(LEATHER_BACKPACK.get()))
                            .displayItems((params, output) -> {
                                Constants.TRAITS_MAP.keySet().forEach(key ->
                                            output.accept(BackpackItem.stackFromKey(key)));
                            })
                            .build());

}