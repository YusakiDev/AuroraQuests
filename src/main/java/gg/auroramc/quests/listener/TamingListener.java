package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.Map;

public class TamingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        AnimalTamer tamer = event.getOwner();
        if (!(tamer instanceof Player player)) {
            return;
        }

        if (player.hasMetadata("NPC")) {
            return;
        }

        Entity entity = event.getEntity();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.TAME, 1, Map.of("type", TypeId.from(entity.getType())));
    }
}
