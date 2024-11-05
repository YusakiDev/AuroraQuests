package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;

public class BuildingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        final Block block = event.getBlock();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BUILD, 1, Map.of("type", TypeId.from(block.getType())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(RegionBlockBreakEvent event) {
        Player player = event.getPlayerWhoBroke();
        if (player.hasMetadata("NPC")) {
            return;
        }
        
        final Block block = event.getBlock();
        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BUILD, -1, Map.of("type", TypeId.from(block.getType())));
    }
}
