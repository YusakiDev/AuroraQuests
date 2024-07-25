package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.reward.Reward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.reward.RewardExecutor;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.event.QuestCompletedEvent;
import gg.auroramc.quests.config.quest.QuestConfig;
import gg.auroramc.quests.config.quest.TaskConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Quest {
    private final QuestConfig config;
    private final List<Reward> rewards = new ArrayList<>();
    private final Set<String> taskTypes;
    private final Map<String, Task> tasks = Maps.newHashMap();
    private final QuestPool holder;

    public Quest(QuestPool holder, QuestConfig config, RewardFactory rewardFactory) {
        this.holder = holder;
        this.config = config;
        for (var key : config.getRewards().getKeys(false)) {
            var reward = rewardFactory.createReward(config.getRewards().getConfigurationSection(key));
            reward.ifPresent(rewards::add);
        }
        taskTypes = config.getTasks().values().stream().map(TaskConfig::getTask).collect(Collectors.toSet());
        for (var task : config.getTasks().entrySet()) {
            tasks.put(task.getKey(), new Task(holder, this, task.getValue(), task.getKey()));
        }
    }

    public String getId() {
        return config.getId();
    }

    public void progress(Player player, String taskType, int count, Map<String, Object> params) {
        if (!taskTypes.contains(taskType)) return;
        if (isCompleted(player)) return;
        if (!canStart(player)) return;
        for (var task : tasks.values()) {
            if (task.getTaskType().equals(taskType)) {
                task.progress(player, count, params);
            }
        }
    }

    public String getDifficulty() {
        return config.getDifficulty();
    }

    public boolean canStart(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);

        if (config.getStartRequirements().getQuests() != null) {
            for (var questId : config.getStartRequirements().getQuests()) {
                var typeId = TypeId.fromString(questId);
                var pool = typeId.namespace().equals("minecraft") ? holder.getId() : typeId.namespace();
                if (!data.hasCompletedQuest(pool, typeId.id())) {
                    return false;
                }
            }
        }

        if (config.getStartRequirements().getPermissions() != null) {
            for (var perm : config.getStartRequirements().getPermissions()) {
                if (!player.hasPermission(perm)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void tryStart(Player player) {
        if (!holder.isGlobal()) return;
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);

        if (canStart(player)) {
            if (!data.hasStartNotification(holder.getId(), getId())) {
                data.setStartNotification(holder.getId(), getId());
                var msg = AuroraQuests.getInstance().getConfigManager().getMessageConfig().getGlobalQuestUnlocked();
                Chat.sendMessage(player, msg, Placeholder.of("{quest}", config.getName()), Placeholder.of("{pool}", holder.getConfig().getName()));
            }
        } else {
            if (data.hasStartNotification(holder.getId(), getId())) {
                data.removeStartNotification(holder.getId(), getId());
            }
        }
    }

    public boolean isCompleted(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        return data.hasCompletedQuest(holder.getId(), getId());
    }

    public boolean canComplete(Player player) {
        return tasks.values().stream().allMatch(task -> task.isCompleted(player));
    }

    public void complete(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        data.completeQuest(holder.getId(), getId());
        data.incrementCompletedCount(holder.getId());
        Bukkit.getPluginManager().callEvent(new QuestCompletedEvent(player, holder, this));
        reward(player);
    }

    private void reward(Player player) {
        // TODO: send messages and stuff
        RewardExecutor.execute(rewards, player, 1, List.of());
    }
}
