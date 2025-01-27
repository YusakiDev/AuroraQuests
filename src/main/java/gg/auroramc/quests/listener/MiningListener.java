package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.Map;
import java.util.Set;

public class MiningListener implements Listener {

    private final Set<Material> blacklist = Set.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL,
            Material.HOPPER, Material.FURNACE, Material.SMOKER, Material.BLAST_FURNACE,
            Material.DISPENSER, Material.ITEM_FRAME, Material.BEACON,
            Material.DROPPER, Material.ARMOR_STAND, Material.BREWING_STAND,
            Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.FLOWER_POT,
            Material.JUKEBOX, Material.LOOM, Material.CARTOGRAPHY_TABLE,
            Material.DECORATED_POT
    );

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent e) {
        if (AuroraAPI.getRegionManager().isPlacedBlock(e.getBlock())) return;
        var player = e.getPlayer();

        if (blacklist.contains(e.getBlockState().getType())) return;

        for (var drop : e.getItems()) {
            var item = drop.getItemStack();
            var id = AuroraAPI.getItemManager().resolveId(item);
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BLOCK_LOOT, item.getAmount(), Map.of("type", id));
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
