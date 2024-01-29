package com.beansgalaxy.backpacks;

import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Traits;
import com.beansgalaxy.backpacks.platform.Services;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Constants {

	public static final String MOD_ID = "beansbackpacks";
	public static final String MOD_NAME = "Beans' Backpacks";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final ModelLayerLocation BACKPACK_MODEL = new ModelLayerLocation(new ResourceLocation(MOD_ID, "backpack_model"), "main");
	public static final ModelLayerLocation POT_MODEL = new ModelLayerLocation(new ResourceLocation(MOD_ID, "pot_player"), "main");
	public static final ResourceLocation IN_LEATHER = new ResourceLocation(MOD_ID, "textures/entity/leather_interior.png");

	public static final HashMap<String, Traits> TRAITS_MAP = new HashMap<>();
	public static final boolean SLOTS_MOD_ACTIVE = Services.COMPAT.anyModsLoaded(new String[]{CompatHelper.CURIOS, CompatHelper.TRINKETS});
	public static final HashSet<Item> CHESTPLATE_DISABLED = new HashSet<>();
	public static final HashSet<Item> DISABLES_BACK_SLOT = new HashSet<>();
	public static final HashSet<Item> BLACKLIST_ITEMS = new HashSet<>();

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

	public static void disableFromChestplate(NonNullList<Item> items) {
		CHESTPLATE_DISABLED.addAll(items);
	}

	public static ItemStack getTorsoWearables(Player player, Item item) {
		ItemStack backSlot = BackData.get(player).getStack();
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return backSlot.is(item) ? backSlot : chestplate;
	}

	public static void disablesBackSlot(NonNullList<Item> items) {
		DISABLES_BACK_SLOT.addAll(items);
	}

	public static void blacklistItems(NonNullList<Item> items) {
		BLACKLIST_ITEMS.addAll(items);
	}

	public static NonNullList<Item> readJsonItemList(ResourceManager resourceManager, String disableChestplate1) {
		Map<ResourceLocation, Resource> disableChestplate = resourceManager.listResources("modify",
				(in) -> in.getPath().endsWith(disableChestplate1));

		NonNullList<Item> items = NonNullList.create();
		disableChestplate.forEach( (resourceLocation, resource) -> {
			try {
				InputStream open = resource.open();
				BufferedReader reader = new BufferedReader(new InputStreamReader(open));

				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.replaceAll(" ", "").split(",");
					for (String id: split) {
						items.add(itemFromString(id));
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		return items;
	}
}