package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.config.CommonConfig;
import com.beansgalaxy.backpacks.config.types.ConfigLabel;
import com.beansgalaxy.backpacks.config.types.ConfigLine;
import com.beansgalaxy.backpacks.config.types.ConfigVariant;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ConfigureConfig implements Packet2C {
      final String encodedConfig;

      private ConfigureConfig(String encodedConfig) {
            this.encodedConfig = encodedConfig;
      }

      public ConfigureConfig(FriendlyByteBuf buf) {
            this(buf.readUtf());
      }

      public static void send(CommonConfig config, ServerPlayer player) {
            Iterator<ConfigLine> iterator = config.getLines().iterator();
            StringBuilder sb = new StringBuilder().append('{');
            while (iterator.hasNext()) {
                  ConfigLine line = iterator.next();
                  if (line instanceof ConfigLabel) continue;

                  String encode = line.encode();
                  sb.append(encode);

                  if (iterator.hasNext())
                        sb.append(',');
            }
            sb.append('}');
            String msg = sb.toString();

            new ConfigureConfig(msg).send2C(player);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.CONFIG_2C;
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(encodedConfig);
      }

      @Override
      public void handle() {
            String jsonContent = encodedConfig.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "");
            ServerSave.CONFIG.parse(jsonContent);
      }
}
