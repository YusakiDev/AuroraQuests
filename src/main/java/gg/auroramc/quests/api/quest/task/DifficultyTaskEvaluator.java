package gg.auroramc.quests.api.quest.task;

import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.entity.Player;

import java.util.Map;

public class DifficultyTaskEvaluator extends TypedTaskEvaluator {
    @Override
    public boolean evaluate(Player player, TaskConfig config, Map<String, Object> params) {
        var res = super.evaluate(player, config, params);
        if (!res) return false;

        if (params.containsKey("difficulty") && params.get("difficulty") instanceof String difficulty) {
            var concreteDifficulty = config.getArgs().getString("difficulty");
            if (concreteDifficulty == null) {
                return true;
            }
            return difficulty.equals(concreteDifficulty);
        }

        return true;
    }
}
