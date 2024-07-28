package gg.auroramc.quests.listener;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class ExpEarnListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpEarn(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();

        if (player.hasMetadata("NPC")) return;

        int amountEarned = e.getAmount();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.EARN_EXP, amountEarned, null);
    }
}
