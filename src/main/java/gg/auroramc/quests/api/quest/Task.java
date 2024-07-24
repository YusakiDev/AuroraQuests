package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.config.quest.TaskConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Task {
    private final QuestPool pool;
    private final Quest holder;
    private final TaskConfig config;
    private final String id;

    public void progress(Player player, int count, Map<String, Object> params) {
        if(!TaskManager.getEvaluator(config.getTask()).evaluate(config, params)) return;

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
}
