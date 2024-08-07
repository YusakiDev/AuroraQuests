package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.util.Version;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.Map;

public class ShearingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        final Entity entity = event.getEntity();

        if (Version.isAtLeastVersion(20, 4)) {
            for (var drop : event.getDrops()) {
                AuroraQuests.getInstance().getQuestManager()
                        .progress(player, TaskType.SHEAR_LOOT, drop.getAmount(), Map.of("type", AuroraAPI.getItemManager().resolveId(drop)));
            }
        }


        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.SHEAR, 1, Map.of("type", TypeId.from(entity.getType())));
    }
}
