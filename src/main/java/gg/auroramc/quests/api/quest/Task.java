package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.entity.Player;

import java.util.Map;

public record Task(QuestPool pool, Quest holder, TaskConfig config, String id) {
    public void progress(Player player, int count, Map<String, Object> params) {
        if (!TaskManager.getEvaluator(config.getTask()).evaluate(config, params)) return;

        AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class)
                .progress(pool.getId(), holder.getId(), id, count);
    }

    public String getTaskType() {
        return config.getTask();
    }

    public boolean isCompleted(Player player) {
        var data = AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class);
        var count = data.getProgression(pool.getId(), holder.getId(), id);
        return count >= config.getArgs().getInt("amount", 1);
    }

    public String getDisplay(Player player) {
        var gc = AuroraQuests.getInstance().getConfigManager().getMainMenuConfig().getTaskStatuses();
        var data = AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class);
        var current = data.getProgression(pool.getId(), holder.getId(), id);
        var required = config.getArgs().getInt("amount", 1);

        return Placeholder.execute(config.getDisplay(),
                Placeholder.of("{status}", isCompleted(player) ? gc.getCompleted() : gc.getNotCompleted()),
                Placeholder.of("{current}", AuroraAPI.formatNumber(current)),
                Placeholder.of("{required}", AuroraAPI.formatNumber(required))
        );
    }
}
