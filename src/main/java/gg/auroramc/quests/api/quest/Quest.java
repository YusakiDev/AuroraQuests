package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.reward.Reward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.reward.RewardExecutor;
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
        for(var task : tasks.values()) {
            if (task.getTaskType().equals(taskType)) {
                task.progress(player, count, params);
            }
        }
    }

    public boolean canStart(Player player) {
        // TODO: check if player can start the quest
        return false;
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
        Bukkit.getPluginManager().callEvent(new QuestCompletedEvent(player, holder, this));
        reward(player);
    }

    private void reward(Player player) {
        // TODO: send messages and stuff
        RewardExecutor.execute(rewards, player, 1, List.of());
    }
}
