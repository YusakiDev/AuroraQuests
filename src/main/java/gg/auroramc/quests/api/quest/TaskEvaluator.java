package gg.auroramc.quests.api.quest;

import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.entity.Player;

import java.util.Map;

public interface TaskEvaluator {
    boolean evaluate(Player player, TaskConfig config, Map<String, Object> params);
}
