package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

      @SubscribeEvent
      public static void onRightClick(InputEvent.InteractionKeyMappingTriggered event) {
            if (event.isUseItem() && event.getHand().equals(InteractionHand.MAIN_HAND)) {
                  Minecraft minecraft = Minecraft.getInstance();
                  LocalPlayer player = minecraft.player;
                  if (!player.isShiftKeyDown() && UseKeyEvent.cauldronPickup(player)) {
                        event.setSwingHand(true);
                        event.setCanceled(true);
                  }
                  else if (minecraft.hitResult instanceof BlockHitResult blockHitResult && UseKeyEvent.cauldronPlace(player, blockHitResult)) {
                        event.setSwingHand(true);
                        event.setCanceled(true);
                  }
                  else if (player.isShiftKeyDown() && UseKeyEvent.cauldronPickup(player)) {
                        event.setSwingHand(true);
                        event.setCanceled(true);
                  }
            }
      }

}
