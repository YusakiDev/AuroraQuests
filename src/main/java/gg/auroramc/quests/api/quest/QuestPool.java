package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.levels.MatcherManager;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.quests.config.quest.PoolConfig;

public class QuestPool {
    private final PoolConfig config;
    private MatcherManager matcherManager;

    public QuestPool(PoolConfig config, RewardFactory rewardFactory) {
        this.config = config;

        if(config.getLeveling().getEnabled()) {
            matcherManager = new MatcherManager(rewardFactory);
            matcherManager.reload(config.getLeveling().getLevelMatchers(), config.getLeveling().getCustomLevels());
        }
    }

    public String getId() {
        return config.getId();
            }

    public boolean hasLeveling() {
        return config.getLeveling().getEnabled();
    }
}
