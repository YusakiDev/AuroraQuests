package gg.auroramc.quests.api.quest;

import gg.auroramc.quests.config.quest.TaskConfig;

import java.util.Map;

public interface TaskEvaluator {
    boolean evaluate(TaskConfig config, Map<String, Object> params);
}
