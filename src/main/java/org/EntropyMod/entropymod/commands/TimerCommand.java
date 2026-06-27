package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.EntropyMod.entropymod.timer.TimerManager;

public class TimerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("timer")
                .then(CommandManager.literal("resume").executes(ctx -> {
                    TimerManager.getInstance().resume();
                    ctx.getSource().sendFeedback(() -> Text.literal("Timer resumed").formatted(Formatting.GREEN), false);
                    return 1;
                }))
                .then(CommandManager.literal("pause").executes(ctx -> {
                    TimerManager.getInstance().pause();
                    ctx.getSource().sendFeedback(() -> Text.literal("Timer paused").formatted(Formatting.YELLOW), false);
                    return 1;
                }))
                .then(CommandManager.literal("stop").executes(ctx -> {
                    TimerManager.getInstance().stop();
                    ctx.getSource().sendFeedback(() -> Text.literal("Timer stopped").formatted(Formatting.RED), false);
                    return 1;
                }))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("seconds", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    TimerManager.getInstance().setTime(IntegerArgumentType.getInteger(ctx, "seconds"));
                                    ctx.getSource().sendFeedback(() -> Text.literal("Timer set").formatted(Formatting.GREEN), false);
                                    return 1;
                                })))
                .then(CommandManager.literal("color")
                        .then(CommandManager.argument("color", StringArgumentType.word())
                                .executes(ctx -> {
                                    TimerManager.getInstance().setColor(StringArgumentType.getString(ctx, "color"));
                                    ctx.getSource().sendFeedback(() -> Text.literal("Color updated").formatted(Formatting.GREEN), false);
                                    return 1;
                                }))));
    }
}
