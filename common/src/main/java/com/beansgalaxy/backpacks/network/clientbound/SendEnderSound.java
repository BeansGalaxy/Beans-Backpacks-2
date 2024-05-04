package com.beansgalaxy.backpacks.network.clientbound;

import com.beansgalaxy.backpacks.client.network.CommonAtClient;
import com.beansgalaxy.backpacks.entity.Kind;
import com.beansgalaxy.backpacks.events.PlaySound;
import com.beansgalaxy.backpacks.network.Network2C;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SendEnderSound implements Packet2C {
      final PlaySound sound;
      final List<BlockPos> posList;
      final double volume;

      public SendEnderSound(PlaySound sound, double volume, List<BlockPos> posList) {
            this.sound = sound;
            this.volume = volume;
            this.posList = posList;
      }

      public SendEnderSound(FriendlyByteBuf buf) {
            this.sound = buf.readEnum(PlaySound.class);
            this.volume = buf.readDouble();
            this.posList = buf.readList(FriendlyByteBuf::readBlockPos);
      }

      @Override
      public Network2C getNetwork() {
            return Network2C.ENDER_SOUND_2C;
      }

      public static void send(List<Entity> owners, PlaySound sound, float volume, ServerLevel level) {
            PlaySound.Playable playable = sound.getSound(Kind.ENDER);
            float range = playable.event().getRange(playable.volume());

            for (ServerPlayer player : level.players()) {
                  ArrayList<BlockPos> posList = new ArrayList<>();
                  double vTotal = 0;
                  double vHighest = 0;
                  for (Entity owner : owners) {
                        if (player.level().dimension() == owner.level().dimension()) {
                              Vec3 pPos = player.position();
                              Vec3 ePos = owner.position();
                              double v = range - ePos.distanceTo(pPos);
                              System.out.println(range + "   " + v);
                              if (v > 0) {
                                    posList.add(BlockPos.containing(ePos));
                                    vTotal += v;
                                    if (v > vHighest)
                                          vHighest = v;
                              }
                        }
                  }

                  if (!posList.isEmpty() && vTotal > 0) {
                        double vFinal = vHighest / vTotal;
                        new SendEnderSound(sound, vFinal * volume, posList).send2C(player);
                  }
            }
      }

      @Override
      public void encode(FriendlyByteBuf buf) {
            buf.writeEnum(sound);
            buf.writeDouble(volume);
            buf.writeCollection(posList, FriendlyByteBuf::writeBlockPos);
      }

      @Override
      public void handle() {
            CommonAtClient.receiveEnderSoundEvent(sound, volume, posList);
      }
}
