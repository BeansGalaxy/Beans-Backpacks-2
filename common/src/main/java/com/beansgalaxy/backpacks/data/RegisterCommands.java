package com.beansgalaxy.backpacks.data;

import com.beansgalaxy.backpacks.data.Config;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterCommands {
      public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            LiteralArgumentBuilder<CommandSourceStack> beansmod = Commands.literal("beansmod");
            registerEnderDataCommand(beansmod);
            registerConfigCommand(beansmod);
            dispatcher.register(beansmod);
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
                                          int size = ServerSave.MAPPED_ENDER_DATA.size();
                                          MutableComponent literal = Component.translatableWithFallback("command.beansbackpacks.enderdata.list.success", "%d entries found: ", size);

                                          boolean addBreak = false;
                                          for (UUID uuid : ServerSave.MAPPED_ENDER_DATA.keySet()) {
                                                if (addBreak)
                                                      literal.append(", ");
                                                else
                                                      literal.append(": ");
                                                addBreak = true;

                                                EnderStorage.Data enderData = ServerSave.MAPPED_ENDER_DATA.get(uuid);
                                                if (enderData.getPlayerName().getContents().toString().equals("empty"))
                                                      literal.append(Component.literal("(" + uuid + ")").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                                                else
                                                      literal.append(enderData.getPlayerNameColored(ctx.getSource().registryAccess()));
                                          }
                                          ctx.getSource().sendSuccess(() -> literal, false);
                                          return size;
                                    })
                        ).then(Commands.literal("clear_player")
                                    .executes(ctx -> {
                                          ServerPlayer player = ctx.getSource().getPlayerOrException();
                                          int sizeS = ServerSave.MAPPED_ENDER_DATA.size();
                                          ServerSave.MAPPED_ENDER_DATA.remove(player.getUUID());
                                          int sizeE = ServerSave.MAPPED_ENDER_DATA.size();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", player.getName()), true);
                                          return sizeE - sizeS;
                                    }).then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> {
                                                      List<ServerPlayer> targets = (List<ServerPlayer>) EntityArgument.getPlayers(ctx, "targets");

                                                      int total = 0;
                                                      for (ServerPlayer player : targets) {
                                                            ServerSave.MAPPED_ENDER_DATA.remove(player.getUUID());
                                                            total++;
                                                      }
                                                      Component who = total == 1 ? targets.get(0).getName() : Component.literal(String.valueOf(total));
                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", who), true);
                                                      return total;
                                                }))
                        ).then(Commands.literal("clear_all")
                                    .executes(ctx -> {
                                          int size = ServerSave.MAPPED_ENDER_DATA.size();
                                          ServerSave.MAPPED_ENDER_DATA.clear();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", size), true);
                                          return size;
                                    }).then(Commands.literal("no_data")
                                                .executes(ctx -> {
                                                      int total = 0;
                                                      for (UUID uuid : ServerSave.MAPPED_ENDER_DATA.keySet()) {
                                                            EnderStorage.Data enderData = ServerSave.MAPPED_ENDER_DATA.get(uuid);
                                                            if (enderData.getItemStacks().isEmpty() && enderData.getTrim().isEmpty()) {
                                                                  ServerSave.MAPPED_ENDER_DATA.remove(uuid);
                                                                  total++;
                                                            }
                                                      }
                                                      MutableComponent literal = Component.literal(String.valueOf(total));
                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", literal), true);
                                                      return total;
                                                })
                                    ).then(Commands.literal("empty_inventories")
                                                .executes(ctx -> {
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : ServerSave.MAPPED_ENDER_DATA.keySet()) {
                                                            EnderStorage.Data enderData = ServerSave.MAPPED_ENDER_DATA.get(uuid);
                                                            if (enderData.getItemStacks().isEmpty())
                                                                  forRemoval.add(uuid);
                                                      }
                                                      int total = forRemoval.size();
                                                      for (UUID uuid : forRemoval)
                                                            ServerSave.MAPPED_ENDER_DATA.remove(uuid);

                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", String.valueOf(total)), true);
                                                      return total;
                                                })
                                    ).then(Commands.literal("empty_name")
                                                .executes(ctx -> {
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : ServerSave.MAPPED_ENDER_DATA.keySet()) {
                                                            EnderStorage.Data enderData = ServerSave.MAPPED_ENDER_DATA.get(uuid);
                                                            if (enderData.getPlayerName().getContents().toString().equals("empty"))
                                                                  forRemoval.add(uuid);
                                                      }
                                                      int total = forRemoval.size();
                                                      for (UUID uuid : forRemoval)
                                                            ServerSave.MAPPED_ENDER_DATA.remove(uuid);

                                                      ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", String.valueOf(total)), true);
                                                      return total;
                                                })
                                    )
                        )
            );
      }
}
