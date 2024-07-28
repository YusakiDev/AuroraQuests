package gg.auroramc.quests.hooks.auroralevels;

import gg.auroramc.levels.api.event.PlayerLevelUpEvent;
import gg.auroramc.levels.api.event.PlayerXpGainEvent;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuroraLevelsListener implements Listener {
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent e) {
        AuroraQuests.getInstance().getQuestManager().progress(e.getPlayer(), TaskType.GAIN_AURORALEVEL, 1, null);
    }

    @EventHandler
    public void onPlayerXpGain(PlayerXpGainEvent e) {
        AuroraQuests.getInstance().getQuestManager().progress(e.getPlayer(), TaskType.GAIN_AURORALEVELS_XP, e.getXp(), null);
    }
}
