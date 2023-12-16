package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.NetworkPackages;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {

      @SubscribeEvent
      public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(NetworkPackages::register);
      }

}
