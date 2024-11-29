package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FarmingListener implements Listener {
    private final List<Material> crops = List.of(Material.WHEAT, Material.POTATOES, Material.CARROTS, Material.BEETROOTS, Material.COCOA, Material.NETHER_WART);

    private final Set<Material> blockCrops = Set.of(Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO);
    public static final Set<Material> specialCrops = Set.of(Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.MUSHROOM_STEM, Material.MELON, Material.PUMPKIN);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHarvest(PlayerHarvestBlockEvent e) {
        // This event will only be called for right click harvestable crops
        for (var item : e.getItemsHarvested()) {
            AuroraQuests.getInstance().getQuestManager()
                    .progress(e.getPlayer(), TaskType.FARM, item.getAmount(), Map.of("type", TypeId.from(item.getType())));
        }
    }

    @EventHandler
    public void onBlockBreak(RegionBlockBreakEvent e) {
        if (!e.isNatural()) return;
        var block = e.getBlock();

        if (specialCrops.contains(e.getBlock().getType())) return;

        if (blockCrops.contains(block.getType())) {
            AuroraQuests.getInstance().getQuestManager().progress(e.getPlayerWhoBroke(), TaskType.FARM, 1, Map.of("type", TypeId.from(block.getType())));
        }
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        if (crops.contains(event.getBlockState().getType())) {
            if (event.getBlockState().getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() != ageable.getMaximumAge()) return;
                for (var drop : event.getItems()) {
                    var item = drop.getItemStack();
                    AuroraQuests.getInstance().getQuestManager()
                            .progress(event.getPlayer(), TaskType.FARM, item.getAmount(), Map.of("type", TypeId.from(item.getType())));
                }
            }
            return;
        }

        if (AuroraAPI.getRegionManager().isPlacedBlock(event.getBlock())) return;

        if (specialCrops.contains(event.getBlockState().getType())) {
            for (var drop : event.getItems()) {
                var item = drop.getItemStack();
                AuroraQuests.getInstance().getQuestManager().progress(event.getPlayer(), TaskType.FARM, item.getAmount(), Map.of("type", TypeId.from(item.getType())));
            }
        }
    }
}
