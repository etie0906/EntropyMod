package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ChallengesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("challenges")
                .executes(ChallengesCommand::openMenu));
    }

    private static int openMenu(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Opening challenges menu..."));
        return 1;
    }
}