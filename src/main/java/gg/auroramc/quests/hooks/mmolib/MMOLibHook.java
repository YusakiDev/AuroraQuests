package gg.auroramc.quests.hooks.mmolib;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;

public class MMOLibHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("mmo_stat"), MMOStatReward.class);

        plugin.getQuestManager().getRewardAutoCorrector()
                .registerCorrector(NamespacedId.fromDefault("mmo_stat"), new MMOStatCorrector(plugin));
    }
}
