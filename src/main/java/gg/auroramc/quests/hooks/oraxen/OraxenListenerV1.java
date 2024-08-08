package gg.auroramc.quests.hooks.oraxen;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class OraxenListenerV1 implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNoteBlockBreak(OraxenNoteBlockBreakEvent e) {
        handleProgression(e.getPlayer(), e.getMechanic().getItemID(), e.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStringBlockBreak(OraxenStringBlockBreakEvent e) {
        handleProgression(e.getPlayer(), e.getMechanic().getItemID(), e.getBlock());
    }

    private void handleProgression(Player player, String id, Block block) {
        if (player == null) return;
        if (id == null) return;
        if (block == null) return;

        if (AuroraAPI.getRegionManager() != null) {
            if (AuroraAPI.getRegionManager().isPlacedBlock(block)) return;
        }

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.BLOCK_BREAK, 1, Map.of("type", new TypeId("oraxen", id)));
    }
}
