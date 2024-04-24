package com.beansgalaxy.backpacks.network;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.network.clientbound.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public enum Network2C {
      SYNC_VIEWERS_2C("sync_viewers_c", new Packet<>(SyncViewers.class, SyncViewers::encode, SyncViewers::new, SyncViewers::handle)),
      SYNC_BACK_SLOT_2C("sync_back_slot_c", new Packet<>(SyncBackSlot.class, SyncBackSlot::encode, SyncBackSlot::new, SyncBackSlot::handle)),
      SYNC_BACK_INV_2C("sync_back_inv_c", new Packet<>(SyncBackInventory.class, SyncBackInventory::encode, SyncBackInventory::new, SyncBackInventory::handle)),
      CONFIG_TRAITS_2C("config_traits_c", new Packet<>(ConfigureTraits.class, ConfigureTraits::encode, ConfigureTraits::new, ConfigureTraits::handle)),
      CONFIG_LISTS_2C("config_lists_c", new Packet<>(ConfigureLists.class, ConfigureLists::encode, ConfigureLists::new, ConfigureLists::handle)),
      ENDER_POS_2C("ender_pos_c", new Packet<>(ReceiveEnderPos.class, ReceiveEnderPos::encode, ReceiveEnderPos::new, ReceiveEnderPos::handle)),
      SEND_ENDER_DATA_2C("ender_data_c", new Packet<>(SendEnderData.class, SendEnderData::encode, SendEnderData::new, SendEnderData::handle)),
      EQUIP_LOCKED_MSG("equip_locked_msg_c", new Packet<>(EquipLockedMsg.class, EquipLockedMsg::encode, EquipLockedMsg::new, EquipLockedMsg::handle));

      public final Packet<? extends Packet2C> packet;
      public final ResourceLocation id;

      Network2C(String id, Packet<? extends Packet2C> packet) {
            this.id = new ResourceLocation(Constants.MOD_ID, id);
            this.packet = packet;
      }

      public void debugMsgEncode() {
      }

      public void debugMsgDecode() {
      }

      public record Packet<T extends Packet2C>(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, Consumer<T> handle)
      {
      }
}
