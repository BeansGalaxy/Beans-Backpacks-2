package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.CommonClass;
import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

      @SubscribeEvent
      public static void onRightClick(InputEvent.InteractionKeyMappingTriggered event) {
            if (event.isUseItem() && event.getHand().equals(InteractionHand.MAIN_HAND)) {
                  Minecraft minecraft = Minecraft.getInstance();
                  LocalPlayer player = minecraft.player;
                  HitResult hitResult = minecraft.hitResult;
                  if(UseKeyEvent.tryUseCauldron(player, hitResult)) {
                        event.setSwingHand(true);
                        event.setCanceled(true);
                  }
            }
      }

}
