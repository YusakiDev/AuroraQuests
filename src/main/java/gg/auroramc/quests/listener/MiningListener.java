package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.Map;

public class MiningListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent e) {
        if (AuroraAPI.getRegionManager().isPlacedBlock(e.getBlock())) return;
        var player = e.getPlayer();

        for (var drop : e.getItems()) {
            var item = drop.getItemStack();
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BLOCK_LOOT, item.getAmount(), Map.of("type", TypeId.from(item.getType())));
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
}
