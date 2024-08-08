package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;

public class MiningListener implements Listener {
    private static final FixedMetadataValue METADATA = new FixedMetadataValue(AuroraQuests.getInstance(), (byte) 1);

    @EventHandler
    public void onBlockBreak(RegionBlockBreakEvent e) {
        if (!e.isNatural()) return;
        var player = e.getPlayerWhoBroke();


        for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BLOCK_LOOT, drop.getAmount(), Map.of("type", TypeId.from(drop.getType())));
        }
    }

    @EventHandler
    public void onBlockBreak2(RegionBlockBreakEvent e) {
        if (!e.isNatural()) return;
        var player = e.getPlayerWhoBroke();
        var type = e.getBlock().getType();

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.BLOCK_BREAK, 1, Map.of("type", TypeId.from(type)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak3(BlockBreakEvent e) {
        // We don't care if region manager is enabled
        if (AuroraAPI.getRegionManager() != null) return;
        if (e.getBlock().hasMetadata("aurora_placed")) return;

        var player = e.getPlayer();
        var type = e.getBlock().getType();

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.BLOCK_BREAK, 1, Map.of("type", TypeId.from(type)));

        for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BLOCK_LOOT, drop.getAmount(), Map.of("type", TypeId.from(drop.getType())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        // We don't care if region manager is enabled
        if (AuroraAPI.getRegionManager() != null) return;

        e.getBlock().setMetadata("aurora_placed", METADATA);
    }
}
