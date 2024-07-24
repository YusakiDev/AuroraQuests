package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.levels.MatcherManager;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.config.quest.PoolConfig;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class QuestPool {
    private final PoolConfig config;
    private MatcherManager matcherManager;
    private final Map<String, Quest> quests = Maps.newHashMap();

    public QuestPool(PoolConfig config, RewardFactory rewardFactory) {
        this.config = config;

        if (config.getLeveling().getEnabled()) {
            matcherManager = new MatcherManager(rewardFactory);
            matcherManager.reload(config.getLeveling().getLevelMatchers(), config.getLeveling().getCustomLevels());
        }

        for (var q : config.getQuests().entrySet()) {
            quests.put(q.getKey(), new Quest(this, q.getValue(), rewardFactory));
        }
    }

    public String getId() {
        return config.getId();
    }

    public boolean hasLeveling() {
        return config.getLeveling().getEnabled();
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    private QuestData getQuestData(Player player) {
        return AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
    }

    public List<Quest> getPlayerQuests(Player player) {
        var data = getQuestData(player);
        var rolledQuests = data.getPoolRollData(config.getId());
        return rolledQuests.quests().stream().map(this::getQuest).toList();
    }

    public int getPlayerLevel(Player player) {
        return getQuestData(player).getPoolLevel(config.getId());
    }

    public Collection<Quest> getQuests() {
        return quests.values();
    }

    public void rollIfNecessary(Player player) {
        if(config.getType().equals("global")) return;
        var data = getQuestData(player);
        // TODO: roll quests if necessary (based on time)
    }
}
