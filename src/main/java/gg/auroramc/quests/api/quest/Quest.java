package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.message.Text;
import gg.auroramc.aurora.api.reward.Reward;
import gg.auroramc.aurora.api.reward.RewardExecutor;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.api.event.QuestCompletedEvent;
import gg.auroramc.quests.api.event.QuestPoolLevelUpEvent;
import gg.auroramc.quests.config.quest.QuestConfig;
import gg.auroramc.quests.config.quest.TaskConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Quest {
    private final QuestConfig config;
    private final Map<String, Reward> rewards = Maps.newHashMap();
    private final Set<String> taskTypes;
    private final Map<String, Task> tasks = Maps.newHashMap();
    private final QuestPool holder;

    public Quest(QuestPool holder, QuestConfig config, RewardFactory rewardFactory) {
        this.holder = holder;
        this.config = config;
        if (config.getRewards() != null) {
            for (var key : config.getRewards().getKeys(false)) {
                var reward = rewardFactory.createReward(config.getRewards().getConfigurationSection(key));
                reward.ifPresent(r -> rewards.put(key, r));
            }
        }
        taskTypes = config.getTasks().values().stream().map(TaskConfig::getTask).collect(Collectors.toSet());
        for (var task : config.getTasks().entrySet()) {
            tasks.put(task.getKey(), new Task(holder, this, task.getValue(), task.getKey()));
        }
    }

    public String getId() {
        return config.getId();
    }

    public void tryTakeItems(Player player) {
        if (!taskTypes.contains(TaskType.TAKE_ITEM)) return;

        if (!holder.isUnlocked(player)) return;
        if (!isUnlocked(player)) return;
        if (isCompleted(player)) return;

        for (var task : tasks.values()) {
            if (task.getTaskType().equals(TaskType.TAKE_ITEM)) {
                task.tryTakeItems(player);
            }
        }

        if (canComplete(player)) {
            complete(player);
        }
    }

    public void progress(Player player, String taskType, double count, Map<String, Object> params) {
        if (!taskTypes.contains(taskType)) return;
        if (isCompleted(player)) return;
        if (!holder.isUnlocked(player)) return;
        if (!isUnlocked(player)) return;

        for (var task : tasks.values()) {
            if (task.getTaskType().equals(taskType)) {
                task.progress(player, count, params);
            }
        }
        if (canComplete(player)) {
            complete(player);
        }
    }

    public void setProgress(Player player, String taskType, double count, Map<String, Object> params) {
        if (!taskTypes.contains(taskType)) return;
        if (isCompleted(player)) return;
        if (!holder.isUnlocked(player)) return;
        if (!isUnlocked(player)) return;

        for (var task : tasks.values()) {
            if (task.getTaskType().equals(taskType)) {
                task.setProgress(player, count, params);
            }
        }
        if (canComplete(player)) {
            complete(player);
        }
    }

    public String getDifficulty() {
        return config.getDifficulty();
    }

    public boolean canStart(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);

        if (config.getStartRequirements() == null) return true;
        if (config.getStartRequirements().isNeedsManualUnlock() && !data.isQuestStartUnlocked(holder.getId(), getId()))
            return false;

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

    public boolean hasStartRequirements() {
        return config.getStartRequirements() != null &&
                ((config.getStartRequirements().getQuests() != null && !config.getStartRequirements().getQuests().isEmpty()) ||
                        (config.getStartRequirements().getPermissions() != null && !config.getStartRequirements().getPermissions().isEmpty()) || config.getStartRequirements().isNeedsManualUnlock());
    }

    public boolean isUnlocked(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        return !holder.isGlobal() || data.isQuestStartUnlocked(holder.getId(), getId()) || !hasStartRequirements();
    }

    public void tryStart(Player player) {
        if (!holder.isGlobal()) return;
        if (isUnlocked(player)) return;

        if (canStart(player)) {
            forceStart(player);
        }
    }

    public void forceStart(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        data.setQuestStartUnlock(holder.getId(), getId());
        var msg = AuroraQuests.getInstance().getConfigManager().getMessageConfig().getGlobalQuestUnlocked();
        Chat.sendMessage(player, msg, Placeholder.of("{quest}", config.getName()), Placeholder.of("{pool}", holder.getConfig().getName()));
    }

    public boolean isCompleted(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
        return data.hasCompletedQuest(holder.getId(), getId());
    }

    public boolean canComplete(Player player) {
        return tasks.values().stream().allMatch(task -> task.isCompleted(player));
    }

    public void reset(Player player) {
        var user = AuroraAPI.getUserManager().getUser(player);
        var data = user.getData(QuestData.class);
        data.resetQuestProgress(holder.getId(), getId());
    }

    public void complete(Player player) {
        var level = holder.getPlayerLevel(player);

        var user = AuroraAPI.getUserManager().getUser(player);
        var data = user.getData(QuestData.class);
        data.completeQuest(holder.getId(), getId());
        data.incrementCompletedCount(holder.getId());
        if (!holder.isGlobal() || AuroraQuests.getInstance().getConfigManager().getConfig().getLeaderboards().getIncludeGlobal()) {
            AuroraAPI.getLeaderboards().updateUser(user, "quests_" + holder.getId());
        }
        Bukkit.getPluginManager().callEvent(new QuestCompletedEvent(player, holder, this));
        reward(player);

        var newLevel = holder.getPlayerLevel(player);
        if (holder.hasLeveling() && newLevel > level) {
            holder.reward(player, newLevel);
            Bukkit.getPluginManager().callEvent(new QuestPoolLevelUpEvent(player, holder));
        }
    }

    public List<Placeholder<?>> getPlaceholders(Player player) {
        var gc = AuroraQuests.getInstance().getConfigManager().getConfig();
        var menuConfig = AuroraQuests.getInstance().getConfigManager().getCommonMenuConfig();
        List<Placeholder<?>> placeholders = new ArrayList<>(9 + tasks.size() + rewards.size());

        placeholders.add(Placeholder.of("{name}", config.getName()));
        placeholders.add(Placeholder.of("{difficulty}", gc.getDifficulties().get(config.getDifficulty())));
        placeholders.add(Placeholder.of("{difficulty_id}", config.getDifficulty()));
        placeholders.add(Placeholder.of("{quest_id}", config.getId()));
        placeholders.add(Placeholder.of("{quest}", config.getName()));
        placeholders.add(Placeholder.of("{pool_id}", holder.getId()));
        placeholders.add(Placeholder.of("{pool}", holder.getConfig().getName()));
        placeholders.add(Placeholder.of("{player}", player.getName()));
        placeholders.add(Placeholder.of("{pool_level}", holder.getPlayerLevel(player)));

        // Add dependency quest placeholders
        if (config.getStartRequirements() != null && config.getStartRequirements().getQuests() != null) {
            int index = 0;
            for (var questId : config.getStartRequirements().getQuests()) {
                var typeId = TypeId.fromString(questId);
                var poolId = typeId.namespace().equals("minecraft") ? holder.getId() : typeId.namespace();
                var id = typeId.id();
                
                // Check if quest is completed
                boolean isCompleted = AuroraAPI.getUserManager().getUser(player).getData(QuestData.class).hasCompletedQuest(poolId, id);
                
                // Find the quest pool and quest
                var questPool = AuroraQuests.getInstance().getQuestManager().getQuestPool(poolId);
                if (questPool != null) {
                    var depQuest = questPool.getQuest(id);
                    String questName = (depQuest != null) ? depQuest.getConfig().getName() : id;
                    
                    // Add status placeholder using the same format as tasks
                    String status = isCompleted ? 
                        menuConfig.getTaskStatuses().getCompleted() : 
                        menuConfig.getTaskStatuses().getNotCompleted();
                    
                    // Create a display using the same pattern as task display
                    String display = "{status} &f" + questName;
                    
                    // Only keep the most useful placeholders
                    placeholders.add(Placeholder.of("{dep_quest_" + index + "}", 
                        Placeholder.execute(display, Placeholder.of("{status}", status))));
                    
                    index++;
                }
            }
            placeholders.add(Placeholder.of("{dep_quest_count}", index));
        } else {
            placeholders.add(Placeholder.of("{dep_quest_count}", 0));
        }

        for (var task : tasks.values()) {
            placeholders.add(Placeholder.of("{task_" + task.id() + "}", task.getDisplay(player)));
        }

        for (var reward : rewards.entrySet()) {
            placeholders.add(Placeholder.of("{reward_" + reward.getKey() + "}", reward.getValue().getDisplay(player, placeholders)));
        }

        return placeholders;
    }

    private void reward(Player player) {
        var gConfig = AuroraQuests.getInstance().getConfigManager().getConfig();

        List<Placeholder<?>> placeholders = getPlaceholders(player);

        if (gConfig.getQuestCompleteMessage().getEnabled()) {

            var text = Component.text();

            var messageLines = gConfig.getQuestCompleteMessage().getMessage();

            for (var line : messageLines) {
                if (line.equals("component:rewards")) {
                    if (!rewards.isEmpty()) {
                        text.append(Text.component(player, gConfig.getDisplayComponents().get("rewards").getTitle(), placeholders));
                    }
                    for (var reward : rewards.values()) {
                        text.append(Component.newline());
                        var display = gConfig.getDisplayComponents().get("rewards").getLine().replace("{reward}", reward.getDisplay(player, placeholders));
                        text.append(Text.component(player, display, placeholders));
                    }
                } else {
                    text.append(Text.component(player, line, placeholders));
                }

                if (!line.equals(messageLines.getLast())) text.append(Component.newline());
            }

            player.sendMessage(text);
        }

        if (gConfig.getQuestCompleteSound().getEnabled()) {
            var sound = gConfig.getQuestCompleteSound();
            var key = NamespacedKey.fromString(sound.getSound());
            if (key != null) {
                var realSound = Registry.SOUNDS.get(key);
                if (realSound != null) {
                    player.playSound(player.getLocation(),
                            realSound,
                            sound.getVolume(),
                            sound.getPitch());
                }
            } else {
                AuroraQuests.logger().warning("Invalid sound key: " + sound.getSound());
            }
        }

        RewardExecutor.execute(rewards.values().stream().toList(), player, 1, placeholders);
    }
}
