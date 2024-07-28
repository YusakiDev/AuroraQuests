package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MiningListener implements Listener {
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
}
