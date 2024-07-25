package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.message.Text;
import gg.auroramc.aurora.api.reward.Reward;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.reward.RewardExecutor;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.event.QuestCompletedEvent;
import gg.auroramc.quests.config.quest.QuestConfig;
import gg.auroramc.quests.config.quest.TaskConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

    private boolean hasStartRequirements() {
        return (config.getStartRequirements().getQuests() != null && !config.getStartRequirements().getQuests().isEmpty()) ||
                (config.getStartRequirements().getPermissions() != null && !config.getStartRequirements().getPermissions().isEmpty());
    }

    public boolean isUnlocked(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        return data.isQuestStartUnlocked(holder.getId(), getId());
    }

    public void tryStart(Player player) {
        if (!holder.isGlobal()) return;
        if (!hasStartRequirements()) return;
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        if (data.isQuestStartUnlocked(holder.getId(), getId())) return;

        if (canStart(player)) {
            data.setQuestStartUnlock(holder.getId(), getId());
            var msg = AuroraQuests.getInstance().getConfigManager().getMessageConfig().getGlobalQuestUnlocked();
            Chat.sendMessage(player, msg, Placeholder.of("{quest}", config.getName()), Placeholder.of("{pool}", holder.getConfig().getName()));
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
        var gConfig = AuroraQuests.getInstance().getConfigManager().getConfig();

        List<Placeholder<?>> placeholders = List.of(
                Placeholder.of("{quest}", config.getName()),
                Placeholder.of("{quest_id}", config.getId()),
                Placeholder.of("{pool}", holder.getConfig().getName()),
                Placeholder.of("{pool_id}", holder.getConfig().getId()),
                Placeholder.of("{player}", player.getName()),
                Placeholder.of("{difficulty_id}", config.getDifficulty())
        );

        if (gConfig.getQuestCompleteMessage().getEnabled()) {

            var text = Component.text();

            var messageLines = gConfig.getQuestCompleteMessage().getMessage();

            for (var line : messageLines) {
                if (line.equals("component:rewards")) {
                    if (!rewards.isEmpty()) {
                        text.append(Text.component(player, gConfig.getDisplayComponents().get("rewards").getTitle(), placeholders));
                    }
                    for (var reward : rewards) {
                        text.append(Component.newline());
                        var display = gConfig.getDisplayComponents().get("rewards").getLine().replace("{reward}", reward.getDisplay(player, placeholders));
                        text.append(Text.component(player, display, placeholders));
                    }
                } else {
                    text.append(Text.component(player, line, placeholders));
                }

                if (!line.equals(messageLines.getLast())) text.append(Component.newline());
            }

            Chat.sendMessage(player, "", Placeholder.of("{quest}", config.getName()), Placeholder.of("{pool}", holder.getConfig().getName()));
        }

        if (gConfig.getQuestCompleteSound().getEnabled()) {
            var sound = gConfig.getQuestCompleteSound();
            player.playSound(player.getLocation(),
                    Sound.valueOf(sound.getSound().toUpperCase()),
                    sound.getVolume(),
                    sound.getPitch());
        }

        RewardExecutor.execute(rewards, player, 1, placeholders);
    }
}
