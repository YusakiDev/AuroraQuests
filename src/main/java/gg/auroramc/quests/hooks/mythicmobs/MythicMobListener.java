package gg.auroramc.quests.hooks.mythicmobs;

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
        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.KILL_MYTHIC_MOB, 1, Map.of("type", new TypeId("mythicmobs", mobName)));

    }
}
