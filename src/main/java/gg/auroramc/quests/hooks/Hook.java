package gg.auroramc.quests.hooks;

import gg.auroramc.quests.AuroraQuests;

public interface Hook {
    void hook(AuroraQuests plugin);

    default void hookAtStartUp(AuroraQuests plugin) {
    }
}
