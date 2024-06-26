package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.serverbound.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum Network2S {
      ACTION_KEY_2S("action_key_s", new Packet<>(SyncActionKey.class, SyncActionKey::encode, SyncActionKey::new, SyncActionKey::handle)),
      INSTANT_PLACE_2S("instant_place_s", new Packet<>(InstantPlace.class, InstantPlace::encode, InstantPlace::new, InstantPlace::handle)),
      PICK_BACKPACK_2S("pick_backpack_s", new Packet<>(PickBackpack.class, PickBackpack::encode, PickBackpack::new, PickBackpack::handle)),
      USE_CAULDRON_2S("use_cauldron_s", new Packet<>(UseCauldron.class, UseCauldron::encode, UseCauldron::new, UseCauldron::handle)),
      CLEAR_BACK_SLOT_2S("clear_back_slot_s", new Packet<>(ClearBackSlot.class, ClearBackSlot::encode, ClearBackSlot::new, ClearBackSlot::handle));

      public final Packet<? extends Packet2S> packet;
      public final ResourceLocation id;

      Network2S(String id, Packet<? extends Packet2S> packet) {
            this.id = new ResourceLocation(Constants.MOD_ID, id);
            this.packet = packet;
      }

      public void debugMsgEncode() {
      }

      public void debugMsgDecode() {
      }

      public record Packet<T extends Packet2S>(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, ServerPlayer> handle)
      {
      }
}
