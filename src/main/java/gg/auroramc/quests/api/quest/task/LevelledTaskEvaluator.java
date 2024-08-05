package gg.auroramc.quests.api.quest.task;

import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.entity.Player;

import java.util.Map;

public class LevelledTaskEvaluator extends TypedTaskEvaluator {
    @Override
    public boolean evaluate(Player player, TaskConfig config, Map<String, Object> params) {
        var res = super.evaluate(player, config, params);
        if (!res) return false;

        if (params.containsKey("level") && params.get("level") instanceof Double level) {
            var concreteLevel = config.getArgs().getDouble("level", -1);
            if (concreteLevel != -1) {
                return level == concreteLevel;
            }
            var min = config.getArgs().getDouble("min-level", 0);
            var max = config.getArgs().getDouble("max-level", Integer.MAX_VALUE);
            return level >= min && level <= max;
        }

        return true;
    }
}
