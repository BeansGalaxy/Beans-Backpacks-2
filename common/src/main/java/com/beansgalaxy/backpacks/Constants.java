package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.data.Traits;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Constants {
	public static final String MOD_ID = "beansbackpacks";
	public static final String MOD_NAME = "Beans' Backpacks";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final HashMap<String, Traits> TRAITS_MAP = new HashMap<>();
	public static final boolean SLOTS_MOD_ACTIVE = Services.COMPAT.anyModsLoaded(new String[]{CompatHelper.CURIOS, CompatHelper.TRINKETS});
	public static final HashSet<Item> CHESTPLATE_DISABLED = new HashSet<>();
	public static final HashSet<Item> DISABLES_BACK_SLOT = new HashSet<>();
	public static final HashSet<Item> BLACKLIST_ITEMS = new HashSet<>();
	public static final HashSet<Item> ELYTRA_ITEMS = new HashSet<>();

	public static final CreativeModeTab.DisplayItemsGenerator CREATIVE_TAB_GENERATOR = (params, output) -> {
		for (Kind value : Kind.values()) {
			if (!Kind.UPGRADED.is(value))
				output.accept(value.getItem());
			if (Kind.METAL.is(value))
				Constants.TRAITS_MAP.keySet().forEach(key -> {
					if (!key.equals("null"))
						output.accept(Traits.toStack(key));
				});
		}
	};

	public static ItemStack createLabeledBackpack(String backpack_id) {
		ItemStack backpackStack = Services.REGISTRY.getMetal().getDefaultInstance();
		backpackStack.getOrCreateTag().putString("backpack_id", backpack_id);
		return backpackStack;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty() || string.isBlank();
	}

	public static boolean isEmpty(Component component) {
		return component == null || component.getContents().toString().equals("empty");
	}

	public static boolean elytraOrDisables(Item item) {
		return DISABLES_BACK_SLOT.contains(item) || ELYTRA_ITEMS.contains(item);
	}

	public static boolean canEquipWithBackpack(Item item) {
		return DISABLES_BACK_SLOT.contains(item) || ELYTRA_ITEMS.contains(item) || CHESTPLATE_DISABLED.contains(item);
	}

	protected static void register() {
		LOG.info("Initializing Beans' Backpacks Constants");
	}

	public static Item itemFromString(String string) {
		if (string == null)
			return Items.AIR;
		String[] location = string.split(":");
		ResourceLocation resourceLocation = new ResourceLocation(location[0], location[1]);
            return BuiltInRegistries.ITEM.get(resourceLocation);
	}

	public static void addToList(HashSet<Item> list, Collection<Item> items) {
		list.addAll(items);
		list.removeIf(item -> item.equals(Items.AIR));
	}

	public static NonNullList<Item> readItemList(ResourceManager resourceManager, String location) {
		Map<ResourceLocation, Resource> locations = resourceManager.listResources("modify",
				(in) -> in.getPath().endsWith(location));

		NonNullList<Item> items = NonNullList.create();
		NonNullList<Item> removedItems = NonNullList.create();
		locations.forEach( (resourceLocation, resource) -> {
			try {
				InputStream open = resource.open();
				BufferedReader reader = new BufferedReader(new InputStreamReader(open));

				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.replaceAll(" ", "").split(",");
					for (String id: split) {
						if (id.startsWith("!"))
							removedItems.add(itemFromString(id.replace("!", "")));
						else
							items.add(itemFromString(id));
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		removedItems.add(Items.AIR.asItem());
		items.removeAll(removedItems);
		return items;
	}

	public static HashSet<String> readStringList(ResourceManager resourceManager, String location) {
		Map<ResourceLocation, Resource> locations = resourceManager.listResources("modify",
				(in) -> in.getPath().endsWith(location));

		HashSet<String> strings = new HashSet<>();
		HashSet<String> removedStrings = new HashSet<>();
		locations.forEach( (resourceLocation, resource) -> {
			try {
				InputStream open = resource.open();
				BufferedReader reader = new BufferedReader(new InputStreamReader(open));

				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.replaceAll(" ", "").split(",");
					for (String id: split) {
						if (id.startsWith("!"))
							removedStrings.add(id.replace("!", ""));
						else
							strings.add(id);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		strings.removeIf(String::isEmpty);
		strings.removeAll(removedStrings);
		return strings;

	}
}