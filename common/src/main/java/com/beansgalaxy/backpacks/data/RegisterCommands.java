package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.Constants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class RegisterCommands {
      public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            LiteralArgumentBuilder<CommandSourceStack> beansmod = Commands.literal("beansmod");
            registerEnderDataCommand(beansmod);
            registerConfigCommand(beansmod);
            registerGiveCommand(beansmod);
            dispatcher.register(beansmod);
      }

      private static void registerGiveCommand(LiteralArgumentBuilder<CommandSourceStack> beansmod) {
            beansmod.then(Commands.literal("give").requires(in -> in.hasPermission(4))
                        .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("backpack_id", StringArgumentType.word())
                                          .executes(ctx -> {
                                                String backpack_id = StringArgumentType.getString(ctx, "backpack_id");
                                                Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
                                                if (!Constants.TRAITS_MAP.containsKey(backpack_id)) {
                                                      ctx.getSource().sendFailure(Component.translatable("command.beansbackpacks.give.fail.no_id", backpack_id));
                                                      return -1;
                                                }

                                                if (targets.isEmpty()) {
                                                      ctx.getSource().sendFailure(Component.translatable("command.beansbackpacks.give.fail.no_players"));
                                                      return -1;
                                                }

                                                ItemStack backpackStack = Constants.createLabeledBackpack(backpack_id);
                                                Component playerNames = null;
                                                for (ServerPlayer player : targets) {
                                                      player.getInventory().add(backpackStack.copy());
                                                      if (playerNames != null) {
                                                            playerNames.plainCopy().append(", ").append(player.getName());
                                                      } else {
                                                            playerNames = player.getDisplayName();
                                                      }
                                                }

                                                Component finalPlayerNames = playerNames;
                                                ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.give.success", backpackStack.getDisplayName(), finalPlayerNames), true);
                                                return targets.size();
                                          })
                                    )
                        )
            );
      }

      private static void registerConfigCommand(LiteralArgumentBuilder<CommandSourceStack> beansmod) {
            beansmod.then(Commands.literal("config").requires(in -> in.hasPermission(4))
                        .then(Commands.literal(Config.UNBIND_ENDER_ON_DEATH.readable()).then(Commands.argument(Config.UNBIND_ENDER_ON_DEATH.readable(), BoolArgumentType.bool())
                                    .executes(ctx -> {
                                          String readable = Config.UNBIND_ENDER_ON_DEATH.readable();
                                          boolean newValue = BoolArgumentType.getBool(ctx, readable);

                                          if (ServerSave.CONFIG.get(Config.UNBIND_ENDER_ON_DEATH).equals(newValue)) {
                                                ctx.getSource().sendFailure(Component.translatable("command.beansbackpacks.config.fail", readable, newValue));
                                                return -1;
                                          }

                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.config.success", readable, newValue), true);
                                          ServerSave.CONFIG.put(Config.UNBIND_ENDER_ON_DEATH, newValue);
                                          return 1;
                                    })
                        )).then(Commands.literal(Config.ENDER_LOCK_LOGGED_OFF.readable()).then(Commands.argument(Config.ENDER_LOCK_LOGGED_OFF.readable(), BoolArgumentType.bool())
                                    .executes(ctx -> {
                                          String readable = Config.ENDER_LOCK_LOGGED_OFF.readable();
                                          boolean newValue = BoolArgumentType.getBool(ctx, readable);

                                          if (ServerSave.CONFIG.get(Config.ENDER_LOCK_LOGGED_OFF).equals(newValue)) {
                                                ctx.getSource().sendFailure(Component.translatable("command.beansbackpacks.config.fail", readable, newValue));
                                                return -1;
                                          }

                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.config.success", readable, newValue), true);
                                          ServerSave.CONFIG.put(Config.ENDER_LOCK_LOGGED_OFF, newValue);
                                          return 1;
                                    })
                        ))
            );
      }

      private static void registerEnderDataCommand(LiteralArgumentBuilder<CommandSourceStack> beansmod) {
            beansmod.then(Commands.literal("enderdata").requires(in -> in.hasPermission(4))
                        .then(Commands.literal("list")
                                    .executes(ctx -> {
                                          HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                          int size = enderStorage.size();
                                          MutableComponent literal = Component.translatableWithFallback("command.beansbackpacks.enderdata.list.success", "%d entries found: ", size);

                                          boolean addBreak = false;
                                          for (UUID uuid : enderStorage.keySet()) {
                                                if (addBreak)
                                                      literal.append(", ");
                                                else
                                                      literal.append(": ");
                                                addBreak = true;

                                                EnderStorage.Data enderData = enderStorage.get(uuid);
                                                if (Constants.isEmpty(enderData.getPlayerName()))
                                                      literal.append(Component.literal("(" + uuid + ")").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                                                else
                                                      literal.append(enderData.getPlayerNameColored(ctx.getSource().registryAccess()));
                                          }
                                          ctx.getSource().sendSuccess(() -> literal, false);
                                          return size;
                                    })
                        ).then(Commands.literal("clear_player")
                                    .executes(ctx -> {
                                          HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                          ServerPlayer player = ctx.getSource().getPlayerOrException();
                                          int sizeS = enderStorage.size();
                                          enderStorage.remove(player.getUUID());
                                          int sizeE = enderStorage.size();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", player.getName()), true);
                                          return sizeE - sizeS;
                                    }).then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> {
                                                      HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                                      List<ServerPlayer> targets = (List<ServerPlayer>) EntityArgument.getPlayers(ctx, "targets");

                                                      int total = 0;
                                                      for (ServerPlayer player : targets) {
                                                            enderStorage.remove(player.getUUID());
                                                            total++;
                                                      }
                                                      Component who = total == 1 ? targets.get(0).getName() : Component.literal(String.valueOf(total));
                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", who), true);
                                                      return total;
                                                }))
                        ).then(Commands.literal("clear_all")
                                    .executes(ctx -> {
                                          HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                          int size = enderStorage.size();
                                          enderStorage.clear();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", size), true);
                                          return size;
                                    }).then(Commands.literal("no_data")
                                                .executes(ctx -> {
                                                      HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                                      int total = 0;
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderStorage.Data enderData = enderStorage.get(uuid);
                                                            if (enderData.getItemStacks().isEmpty() && enderData.getTrim().isEmpty()) {
                                                                  enderStorage.remove(uuid);
                                                                  total++;
                                                            }
                                                      }
                                                      MutableComponent literal = Component.literal(String.valueOf(total));
                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", literal), true);
                                                      return total;
                                                })
                                    ).then(Commands.literal("empty_inventories")
                                                .executes(ctx -> {
                                                      HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderStorage.Data enderData = enderStorage.get(uuid);
                                                            if (enderData.getItemStacks().isEmpty())
                                                                  forRemoval.add(uuid);
                                                      }
                                                      int total = forRemoval.size();
                                                      for (UUID uuid : forRemoval)
                                                            enderStorage.remove(uuid);

                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", String.valueOf(total)), true);
                                                      return total;
                                                })
                                    ).then(Commands.literal("empty_name")
                                                .executes(ctx -> {
                                                      HashMap<UUID, EnderStorage.Data> enderStorage = EnderStorage.get().MAPPED_DATA;
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderStorage.Data enderData = enderStorage.get(uuid);
                                                            if (Constants.isEmpty(enderData.getPlayerName()))
                                                                  forRemoval.add(uuid);
                                                      }
                                                      int total = forRemoval.size();
                                                      for (UUID uuid : forRemoval)
                                                            enderStorage.remove(uuid);

                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", String.valueOf(total)), true);
                                                      return total;
                                                })
                                    )
                        )
            );
      }
}
