package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

public class MobKillingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player == null) return;
        if (player.hasMetadata("NPC")) return;

        var id = AuroraAPI.getEntityManager().resolveId(entity);
        if (id.namespace().equals("mythicmobs")) return;

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.KILL_MOB, 1, Map.of("type", id));
    }
}
