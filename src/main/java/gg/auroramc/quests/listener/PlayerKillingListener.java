package gg.auroramc.quests.listener;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerKillingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (!(mob instanceof Player victim)
                || killer == null
                || killer.hasMetadata("NPC")
                || mob == killer) {
            return;
        }

        AuroraQuests.getInstance().getQuestManager().progress(killer, TaskType.KILL_PLAYER, 1, null);
    }
}
