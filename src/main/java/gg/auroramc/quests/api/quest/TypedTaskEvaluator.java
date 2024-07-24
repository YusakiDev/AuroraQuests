package gg.auroramc.quests.api.quest;

import gg.auroramc.quests.config.quest.TaskConfig;

import java.util.Map;

public class TypedTaskEvaluator implements TaskEvaluator {
    @Override
    public boolean evaluate(TaskConfig config, Map<String, Object> params) {
        var list = config.getArgs().getStringList("types");
        if (list.isEmpty()) return true;
        if (params.containsKey("type")) {
            return list.contains(params.get("type"));
        }
        return false;
    }
}
