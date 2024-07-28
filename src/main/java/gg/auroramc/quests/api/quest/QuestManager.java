package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.reward.CommandReward;
import gg.auroramc.aurora.api.reward.ItemReward;
import gg.auroramc.aurora.api.reward.MoneyReward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        rewardFactory.registerRewardType(NamespacedId.fromDefault("item"), ItemReward.class);
        try {
            StdSchedulerFactory.getDefaultScheduler().start();
        } catch (SchedulerException e) {
            AuroraQuests.logger().severe("Failed to start scheduler: " + e.getMessage());
        }
    }

    public void reload() {
        if (!pools.isEmpty()) {
            pools.values().forEach(QuestPool::dispose);
        }
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

    public List<QuestPool> rollQuestsIfNecessary(Player player) {
        var rolledPools = new ArrayList<QuestPool>();

        for (var pool : pools.values()) {
            if (pool.rollIfNecessary(player, false)) {
                rolledPools.add(pool);
            }
        }

        return rolledPools;
    }

    public void tryStartGlobalQuests(Player player) {
        for (var pool : pools.values()) {
            if (pool.isGlobal()) {
                pool.tryStartGlobalQuests(player);
            }
        }
    }
}
