package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.reward.CommandReward;
import gg.auroramc.aurora.api.reward.MoneyReward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class QuestManager {
    @Getter
    private final RewardFactory rewardFactory = new RewardFactory();

    private final AuroraQuests plugin;
    private final Map<String, QuestPool> pools = Maps.newConcurrentMap();

    public QuestManager(AuroraQuests plugin) {
        this.plugin = plugin;
        rewardFactory.registerRewardType(NamespacedId.fromDefault("command"), CommandReward.class);
        rewardFactory.registerRewardType(NamespacedId.fromDefault("money"), MoneyReward.class);
    }

    public void reload() {
        pools.clear();
        for (var poolEntry : plugin.getConfigManager().getQuestPools().entrySet()) {
            pools.put(poolEntry.getKey(), new QuestPool(poolEntry.getValue(), rewardFactory));
        }
    }

    public void progress(Player player, String taskType, int amount, Map<String, Object> params) {
        for (var pool : pools.values()) {
            for (var quest : pool.getPlayerQuests(player)) {
                quest.progress(player, taskType, amount, params);
                if (quest.canComplete(player)) {
                    quest.complete(player);
                }
            }
        }
    }

    public Collection<QuestPool> getQuestPools() {
        return pools.values();
    }

    public QuestPool getQuestPool(String id) {
        return pools.get(id);
    }

    public void rollQuestsIfNecessary(Player player) {
        for (var pool : pools.values()) {
            pool.rollIfNecessary(player);
        }
    }
}
