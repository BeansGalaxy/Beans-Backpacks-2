package com.beansgalaxy.backpacks.compat;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.core.BackData;
import com.beansgalaxy.backpacks.core.Kind;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public class CurioRegistry {

      public static void register(final FMLCommonSetupEvent event) {
            for (Kind kind: Kind.values())
                  CuriosApi.registerCurio(kind.getItem(), new CurioItem());
            //CuriosApi.registerCurio(Items.ELYTRA, new CurioItem());
      }

      public static void setBackStack(ItemStack stack, Player owner) {
            Optional<ICuriosItemHandler> resolve = CuriosApi.getCuriosInventory(owner).resolve();
            if (resolve.isEmpty())
                  return;


            //resolve.get().setEquippedCurio(); TODO: USE THIS METHOD INSTEAD TO SET BACK SLOT

            IDynamicStackHandler stacks = resolve.get().getCurios().get("back").getStacks();
            if (stacks.getSlots() > 0)
                  stacks.setStackInSlot(0, stack);
      }

      public static ItemStack getBackStackItem(BackData backData, ItemStack stack) {
            Optional<ICuriosItemHandler> resolve = CuriosApi.getCuriosInventory(backData.owner).resolve();
            if (resolve.isEmpty())
                  return stack;

            IDynamicStackHandler stacks = resolve.get().getCurios().get("back").getStacks();
            if (stacks.getSlots() == 0)
                  return stack;

            ItemStack stackInSlot = stacks.getStackInSlot(0);
            if (stack != stackInSlot)
                  backData.update(stackInSlot);

            return stackInSlot;
      }

      public static boolean backSlotDisables(Player owner) {
            Optional<ICuriosItemHandler> resolve = CuriosApi.getCuriosInventory(owner).resolve();
            if (resolve.isEmpty())
                  return false;

            Optional<SlotResult> firstCurio = resolve.get().findFirstCurio(stack -> Constants.elytraOrDisables(stack.getItem()));
            return firstCurio.isPresent();
      }
}
