package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.network.packages.SprintKeyPacket;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;

public class FabricNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            SprintKeyPacket.C2S(sprintKeyPressed);
      }
}
