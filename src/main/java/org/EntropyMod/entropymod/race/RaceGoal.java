package org.EntropyMod.entropymod.race;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface RaceGoal {
    String getId();
    String getName();
    Text getDescription();
    boolean checkCompletion(ServerPlayerEntity player);
    void onStart();
    void reset();
}
