package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.items.BackpackRecipe;
import com.beansgalaxy.backpacks.items.BackpackItem;
import com.beansgalaxy.backpacks.items.DyableBackpack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Constants.MOD_ID)
public class ForgeMain {
    
    public ForgeMain() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(bus);
        RECIPES.register(bus);

        bus.addListener(this::addCreative);

        Constants.LOG.info("Initializing Beans' Backpacks Forge");
        CommonClass.init();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LEATHER_BACKPACK);
            event.accept(METAL_BACKPACK);
            event.accept(UPGRADED_BACKPACK);
        }
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BackpackRecipe>> BACKPACK_RECIPE =
                RECIPES.register(BackpackRecipe.Serializer.ID, () -> BackpackRecipe.Serializer.INSTANCE);

    public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> LEATHER_BACKPACK = ITEMS.register("backpack", DyableBackpack::new);
    public static final RegistryObject<Item> METAL_BACKPACK = ITEMS.register("metal_backpack", BackpackItem::new);
    public static final RegistryObject<Item> UPGRADED_BACKPACK = ITEMS.register("upgraded_backpack", BackpackItem::new);

}