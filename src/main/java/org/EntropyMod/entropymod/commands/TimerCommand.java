package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.EntropyMod.entropymod.timer.TimerManager;

public class TimerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("timer")
                .requires(source -> source.hasPermissionLevel(2)) // OP only
                .then(CommandManager.literal("resume").executes(TimerCommand::resume))
                .then(CommandManager.literal("pause").executes(TimerCommand::pause))
                .then(CommandManager.literal("stop").executes(TimerCommand::stop))
                .then(CommandManager.literal("upwards").executes(TimerCommand::setUpwards))
                .then(CommandManager.literal("downwards").executes(TimerCommand::setDownwards))
                .then(CommandManager.literal("color")
                        .then(CommandManager.argument("type", StringArgumentType.word())
                                .then(CommandManager.argument("color", StringArgumentType.greedyString())
                                        .executes(TimerCommand::setColor))))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("time", StringArgumentType.greedyString())
                                .executes(TimerCommand::setTime)))
        );
    }

    private static int resume(CommandContext<ServerCommandSource> context) {
        TimerManager.getInstance().resume();
        context.getSource().sendFeedback(() -> Text.literal("Timer resumed"), true);
        return 1;
    }

    private static int pause(CommandContext<ServerCommandSource> context) {
        TimerManager.getInstance().pause();
        context.getSource().sendFeedback(() -> Text.literal("Timer paused"), true);
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) {
        TimerManager.getInstance().stop();
        context.getSource().sendFeedback(() -> Text.literal("Timer stopped"), true);
        return 1;
    }

    private static int setUpwards(CommandContext<ServerCommandSource> context) {
        TimerManager.getInstance().setUpwards(true);
        context.getSource().sendFeedback(() -> Text.literal("Timer set to count upwards"), true);
        return 1;
    }

    private static int setDownwards(CommandContext<ServerCommandSource> context) {
        TimerManager.getInstance().setUpwards(false);
        context.getSource().sendFeedback(() -> Text.literal("Timer set to count downwards"), true);
        return 1;
    }

    private static int setColor(CommandContext<ServerCommandSource> context) {
        String type = StringArgumentType.getString(context, "type");
        String color = StringArgumentType.getString(context, "color");
        TimerManager.getInstance().setColor(type, color);
        context.getSource().sendFeedback(() -> Text.literal("Timer color set: " + type + " = " + color), true);
        return 1;
    }

    private static int setTime(CommandContext<ServerCommandSource> context) {
        String timeStr = StringArgumentType.getString(context, "time");
        try {
            // Parse d,hh:mm:ss format
            String[] parts = timeStr.split(",");
            int days = 0;
            String timePart = parts[0];

            if (parts.length > 1) {
                days = Integer.parseInt(parts[0]);
                timePart = parts[1];
            }

            String[] timeParts = timePart.split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            TimerManager.getInstance().setTime(days, hours, minutes, seconds);
            context.getSource().sendFeedback(() -> Text.literal("Timer set to: " + timeStr), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Invalid format! Use: d,hh:mm:ss or hh:mm:ss"));
            return 0;
        }
    }
}
