package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.auroracore.quests.task.TaskType;
import gg.auroramc.auroracore.quests.task.impl.TypedCounterTask;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FarmingListener implements Listener {
    private final List<Material> crops = List.of(Material.WHEAT, Material.POTATOES, Material.CARROTS, Material.BEETROOTS, Material.COCOA, Material.NETHER_WART);

    private final Set<Material> blockCrops = Set.of(Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO);
    private final Set<Material> specialCrops = Set.of(Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.MELON, Material.PUMPKIN);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHarvest(PlayerHarvestBlockEvent e) {
        // This event will only be called for right click harvestable crops
        for (var item : e.getItemsHarvested()) {
            AuroraQuests.getInstance().getQuestManager()
                    .progress(e.getPlayer(), TaskType.FARM, item.getAmount(), Map.of("type", TypeId.from(item.getType())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBlockBreakHarvest(BlockBreakEvent e) {
        if (!crops.contains(e.getBlock().getType())) return;

        if (e.getBlock().getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() != ageable.getMaximumAge()) return;
            for (var drop : e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())) {
                AuroraQuests.getInstance().getQuestManager()
                        .progress(e.getPlayer(), TaskType.FARM, drop.getAmount(), Map.of("type", TypeId.from(drop.getType())));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(RegionBlockBreakEvent e) {
        if (!e.isNatural()) return;
        var player = e.getPlayerWhoBroke();
        var type = e.getBlock().getType();

        if (blockCrops.contains(type)) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.FARM, 1, Map.of("type", TypeId.from(type)));
            return;
        }


        if (specialCrops.contains(type)) {
            for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.FARM, drop.getAmount(), Map.of("type", TypeId.from(drop.getType())));
            }
        }
    }
}
