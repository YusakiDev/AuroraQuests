package gg.auroramc.quests.hooks.mythicmobs.reward;

import io.lumine.mythic.core.skills.stats.StatSource;

public class AuroraQuestsStatSource implements StatSource {
    @Override
    public boolean removeOnReload() {
        return false;
    }
}
