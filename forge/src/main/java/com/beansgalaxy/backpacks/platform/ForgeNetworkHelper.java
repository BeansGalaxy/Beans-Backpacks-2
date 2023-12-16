package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.network.NetworkPackages;
import com.beansgalaxy.backpacks.network.packages.SprintKeyPacketC2S;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;

public class ForgeNetworkHelper implements NetworkHelper {
      @Override
      public void SprintKey(boolean sprintKeyPressed) {
            NetworkPackages.C2S(new SprintKeyPacketC2S(sprintKeyPressed));
      }
}
