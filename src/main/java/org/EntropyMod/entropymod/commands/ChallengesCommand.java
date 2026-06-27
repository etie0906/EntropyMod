package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.EntropyMod.entropymod.challenges.ChallengeManager;

public class ChallengesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("challenge")
                .then(CommandManager.literal("start")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    ChallengeManager.getInstance().getAvailableChallenges().forEach(c ->
                                            builder.suggest(c.getId()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> startChallenge(ctx, 1))
                                .then(CommandManager.argument("stackSize", IntegerArgumentType.integer(1))
                                        .executes(ctx -> startChallenge(ctx,
                                                IntegerArgumentType.getInteger(ctx, "stackSize"))))))
                .then(CommandManager.literal("pause").executes(ChallengesCommand::pauseChallenge))
                .then(CommandManager.literal("resume").executes(ChallengesCommand::resumeChallenge))
                .then(CommandManager.literal("stop").executes(ChallengesCommand::stopChallenge))
                .then(CommandManager.literal("test").executes(ChallengesCommand::testChallenge)));
    }

    private static int startChallenge(CommandContext<ServerCommandSource> context, int stackSize) {
        String id = StringArgumentType.getString(context, "name");
        boolean started = ChallengeManager.getInstance().startChallenge(id);
        if (started) {
            context.getSource().sendFeedback(() ->
                    Text.literal("Challenge " + id + " started!").formatted(Formatting.GREEN), true);
        } else {
            context.getSource().sendError(
                    Text.literal("Challenge could not be started: a challenge is already running or the name is invalid").formatted(Formatting.RED));
        }
        return started ? 1 : 0;
    }

    private static int pauseChallenge(CommandContext<ServerCommandSource> context) {
        ChallengeManager.getInstance().pauseAll();
        context.getSource().sendFeedback(() ->
                Text.literal("Challenge paused").formatted(Formatting.YELLOW), true);
        return 1;
    }

    private static int resumeChallenge(CommandContext<ServerCommandSource> context) {
        ChallengeManager.getInstance().resumeAll();
        context.getSource().sendFeedback(() ->
                Text.literal("Challenge resumed").formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int stopChallenge(CommandContext<ServerCommandSource> context) {
        ChallengeManager.getInstance().stopAll();
        context.getSource().sendFeedback(() ->
                Text.literal("Challenge stopped").formatted(Formatting.RED), true);
        return 1;
    }

    private static int testChallenge(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() ->
                Text.literal("Test command executed").formatted(Formatting.AQUA), false);
        return 1;
    }
}
