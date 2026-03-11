package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TimerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("timer")
                .then(CommandManager.literal("resume").executes(TimerCommand::resume))
                .then(CommandManager.literal("pause").executes(TimerCommand::pause))
                .then(CommandManager.literal("stop").executes(TimerCommand::stop))
                .then(CommandManager.literal("upwards").executes(TimerCommand::setUpwards))
                .then(CommandManager.literal("downwards").executes(TimerCommand::setDownwards))
                .then(CommandManager.literal("color").executes(TimerCommand::setColor))
                .then(CommandManager.literal("time").executes(TimerCommand::setTime)));
    }

    private static int resume(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer resumed"));
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer paused"));
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer stopped"));
        return 1;
    }

    private static int setUpwards(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer set to upwards"));
        return 1;
    }

    private static int setDownwards(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer set to downwards"));
        return 1;
    }

    private static int setColor(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer color set"));
        return 1;
    }

    private static int setTime(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Timer time set"));
        return 1;
    }
}