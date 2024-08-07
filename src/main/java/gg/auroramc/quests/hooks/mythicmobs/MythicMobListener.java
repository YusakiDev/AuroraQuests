package gg.auroramc.quests.hooks.mythicmobs;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class MythicMobListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (!(e.getKiller() instanceof Player player)) return;

        var mobName = e.getMob().getType().getInternalName();
        var drops = e.getDrops();
        var manager = AuroraQuests.getInstance().getQuestManager();

        if (e.getMob().getLevel() > 0) {
            manager.progress(player, TaskType.KILL_LEVELLED_MOB, 1, Map.of(
                    "type", new TypeId("mythicmobs", mobName),
                    "level", Math.floor(e.getMob().getLevel())
            ));
        }

        manager.progress(player, TaskType.KILL_MOB, 1, Map.of(
                "type", new TypeId("mythicmobs", mobName)
        ));

        for (var drop : drops) {
            var typeId = AuroraAPI.getItemManager().resolveId(drop);
            manager.progress(player, TaskType.ENTITY_LOOT, drop.getAmount(), Map.of("type", typeId));
        }

    }
}
