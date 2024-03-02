package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

      @SubscribeEvent
      public static void clientLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
      }

}
