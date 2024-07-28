package gg.auroramc.quests.hooks.auraskills;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class AuraSkillsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillXpGain(XpGainEvent e) {
        var player = e.getPlayer();
        var xp = e.getAmount();
        var skill = e.getSkill().name();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.GAIN_AURASKILLS_XP, xp, Map.of("type", skill));
    }
}
