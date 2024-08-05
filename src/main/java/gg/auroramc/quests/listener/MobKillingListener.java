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
        if (entity instanceof Player) return;
        if (player.hasMetadata("NPC")) return;

        var id = AuroraAPI.getEntityManager().resolveId(entity);
        if (id.namespace().equals("mythicmobs")) return;
        var manager = AuroraQuests.getInstance().getQuestManager();

        manager.progress(player, TaskType.KILL_MOB, 1, Map.of("type", id));

        for (var drop : event.getDrops()) {
            var typeId = AuroraAPI.getItemManager().resolveId(drop);
            manager.progress(player, TaskType.ENTITY_LOOT, drop.getAmount(), Map.of("type", typeId));
        }
    }
}
