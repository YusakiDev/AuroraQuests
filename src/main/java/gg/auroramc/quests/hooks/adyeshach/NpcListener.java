package gg.auroramc.quests.hooks.adyeshach;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class NpcListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteractNPC(AdyeshachEntityInteractEvent event) {
        var npcID = event.getEntity().getId();
        var id = new TypeId("adyeshach", npcID);

        AuroraQuests.getInstance().getQuestManager()
                .progress(event.getPlayer(), TaskType.INTERACT_NPC, 1, Map.of("type", id));
    }
}
