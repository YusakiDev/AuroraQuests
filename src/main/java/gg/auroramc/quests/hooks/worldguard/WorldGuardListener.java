package gg.auroramc.quests.hooks.worldguard;

import gg.auroramc.aurora.api.events.region.PlayerRegionEnterEvent;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class WorldGuardListener implements Listener {
    @EventHandler
    public void onPlayerRegionEnter(PlayerRegionEnterEvent e) {
        var regions = e.getRegions();
        for (var region : regions) {
            AuroraQuests.getInstance().getQuestManager().progress(e.getPlayer(), TaskType.ENTER_REGION, 1, Map.of("type", region.getId()));
        }
    }
}
