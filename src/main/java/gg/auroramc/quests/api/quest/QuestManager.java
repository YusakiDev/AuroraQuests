package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.quests.AuroraQuests;

public class QuestManager {
    private final RewardFactory rewardFactory = new RewardFactory();

    private final AuroraQuests plugin;

    public QuestManager(AuroraQuests plugin) {
        this.plugin = plugin;
    }


}
