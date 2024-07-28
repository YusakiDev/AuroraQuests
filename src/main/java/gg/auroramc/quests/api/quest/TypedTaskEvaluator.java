package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class TypedTaskEvaluator extends FilteredTaskEvaluator {
    @Override
    public boolean evaluate(Player player, TaskConfig config, Map<String, Object> params) {
        var passesFilters = super.evaluate(player, config, params);
        if (!passesFilters) return false;

        var list = config.getArgs().getStringList("types");

        if (list.isEmpty()) return true;

        if (params != null && params.containsKey("type") && params.get("type") instanceof TypeId type) {
            return list.contains(type.toString());
        }

        if (params != null && params.containsKey("type") && params.get("type") instanceof String type) {
            return list.contains(type);
        }
        
        return true;
    }
}
