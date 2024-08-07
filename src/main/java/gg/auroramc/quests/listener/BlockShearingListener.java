package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class BlockShearingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShearBlock(PlayerShearBlockEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        Block block = event.getBlock();

        for (var drop : event.getDrops()) {
            AuroraQuests.getInstance().getQuestManager()
                    .progress(player, TaskType.BLOCK_SHEAR_LOOT, drop.getAmount(), Map.of("type", AuroraAPI.getItemManager().resolveId(drop)));
        }

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BLOCK_SHEAR, 1, Map.of("type", TypeId.from(block.getType())));
    }
}
