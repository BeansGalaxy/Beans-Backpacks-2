package com.beansgalaxy.backpacks.events;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.data.EnderStorage;
import com.beansgalaxy.backpacks.data.ServerSave;
import com.beansgalaxy.backpacks.data.config.Gamerules;
import com.beansgalaxy.backpacks.inventory.EnderInventory;
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
import net.minecraft.world.level.Level;

import java.util.*;

public class RegisterCommands {
      public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            LiteralArgumentBuilder<CommandSourceStack> beansmod = Commands.literal("beansmod");
            registerGameruleCommand(beansmod);
            registerGiveCommand(beansmod);
            registerEnderDataCommand(beansmod);
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

      private static void registerGameruleCommand(LiteralArgumentBuilder<CommandSourceStack> beansmod) {
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("gamerule").requires(in -> in.hasPermission(4));
            for (Gamerules value : Gamerules.values()) {
                  builder.then(Commands.literal(value.readable()).then(Commands.argument(value.readable(), BoolArgumentType.bool())
                              .executes(ctx -> {
                                    String readable = value.readable();
                                    boolean newValue = BoolArgumentType.getBool(ctx, readable);

                                    if (ServerSave.GAMERULES.get(value).equals(newValue)) {
                                          ctx.getSource().sendFailure(Component.translatable("command.beansbackpacks.config.fail", readable, newValue));
                                          return -1;
                                    }

                                    ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.config.success", readable, newValue), true);
                                    ServerSave.GAMERULES.put(value, newValue);
                                    return 1;
                              })
                  ));
            }
            beansmod.then(builder);
      }

      private static void registerEnderDataCommand(LiteralArgumentBuilder<CommandSourceStack> beansmod) {
            beansmod.then(Commands.literal("enderdata").requires(in -> in.hasPermission(4))
                        .then(Commands.literal("list")
                                    .executes(ctx -> {
                                          Level level = ctx.getSource().getPlayer().level();
                                          HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                          int size = enderStorage.size();
                                          MutableComponent literal = Component.translatableWithFallback("command.beansbackpacks.enderdata.list.success", "%d entries found: ", size);

                                          boolean addBreak = false;
                                          for (UUID uuid : enderStorage.keySet()) {
                                                if (addBreak)
                                                      literal.append(", ");
                                                else
                                                      literal.append(": ");
                                                addBreak = true;

                                                EnderInventory enderData = enderStorage.get(uuid);
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
                                          Level level = ctx.getSource().getPlayer().level();
                                          HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                          ServerPlayer player = ctx.getSource().getPlayerOrException();
                                          int sizeS = enderStorage.size();
                                          enderStorage.remove(player.getUUID());
                                          int sizeE = enderStorage.size();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", player.getName()), true);
                                          return sizeE - sizeS;
                                    }).then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> {
                                                      Level level = ctx.getSource().getPlayer().level();
                                                      HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
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
                                          Level level = ctx.getSource().getPlayer().level();
                                          HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                          int size = enderStorage.size();
                                          enderStorage.clear();
                                          ctx.getSource().sendSuccess(() -> Component.translatable("command.beansbackpacks.enderdata.clear.success", size), true);
                                          return size;
                                    }).then(Commands.literal("no_data")
                                                .executes(ctx -> {
                                                      Level level = ctx.getSource().getPlayer().level();
                                                      HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                                      int total = 0;
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderInventory enderData = enderStorage.get(uuid);
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
                                                      Level level = ctx.getSource().getPlayer().level();
                                                      HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderInventory enderData = enderStorage.get(uuid);
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
                                                      Level level = ctx.getSource().getPlayer().level();
                                                      HashMap<UUID, EnderInventory> enderStorage = EnderStorage.get(level).MAP;
                                                      List<UUID> forRemoval = new ArrayList<>();
                                                      for (UUID uuid : enderStorage.keySet()) {
                                                            EnderInventory enderData = enderStorage.get(uuid);
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
