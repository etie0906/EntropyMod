package org.EntropyMod.entropymod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.EntropyMod.entropymod.Entropymod;
import org.EntropyMod.entropymod.network.ChallengePackets;

public class ChallengesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("challenges")
                .then(CommandManager.literal("menu")
                        .executes(ChallengesCommand::openMenu))
        );
    }

    private static int openMenu(CommandContext<ServerCommandSource> context) {
        return 0;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    }
}
