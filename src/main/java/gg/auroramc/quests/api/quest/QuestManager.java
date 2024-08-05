package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.reward.CommandReward;
import gg.auroramc.aurora.api.reward.ItemReward;
import gg.auroramc.aurora.api.reward.MoneyReward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.task.LevelledTaskEvaluator;
import gg.auroramc.quests.hooks.HookManager;
import gg.auroramc.quests.hooks.worldguard.WorldGuardHook;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;

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

        TaskManager.registerEvaluator(TaskType.ENCHANT, new LevelledTaskEvaluator());
        TaskManager.registerEvaluator(TaskType.KILL_LEVELLED_MOB, new LevelledTaskEvaluator());

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

    public void progress(Player player, String taskType, double amount, Map<String, Object> params) {
        if (!player.hasPermission("aurora.quests.use")) return;
        if (plugin.getConfigManager().getConfig().getPreventCreativeMode() && player.getGameMode() == GameMode.CREATIVE)
            return;
        var user = AuroraAPI.getUserManager().getUser(player);
        if (!user.isLoaded()) return;

        if (HookManager.isEnabled(WorldGuardHook.class)) {
            if (HookManager.getHook(WorldGuardHook.class).isBlocked(player)) return;
        }

        for (var pool : pools.values()) {
            for (var quest : pool.getPlayerQuests(player)) {
                quest.progress(player, taskType, amount, params);
            }
        }
    }

    public void progress(Player player, Set<String> taskTypes, double amount, Map<String, Object> params) {
        if (!player.hasPermission("aurora.quests.use")) return;
        if (plugin.getConfigManager().getConfig().getPreventCreativeMode() && player.getGameMode() == GameMode.CREATIVE)
            return;
        var user = AuroraAPI.getUserManager().getUser(player);
        if (!user.isLoaded()) return;

        if (HookManager.isEnabled(WorldGuardHook.class)) {
            if (HookManager.getHook(WorldGuardHook.class).isBlocked(player)) return;
        }

        for (var pool : pools.values()) {
            for (var quest : pool.getPlayerQuests(player)) {
                for (var taskType : taskTypes) {
                    quest.progress(player, taskType, amount, params);
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
