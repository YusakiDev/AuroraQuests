package gg.auroramc.quests.api;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.QuestManager;

public class AuroraQuestsProvider {

    public static QuestManager getQuestManager() {
        return AuroraQuests.getInstance().getQuestManager();
    }
}
