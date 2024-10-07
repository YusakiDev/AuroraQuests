package gg.auroramc.quests.hooks.fancynpcs;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class NpcListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteractNPC(NpcInteractEvent event) {
        var npcID = event.getNpc().getData().getName();
        var id = new TypeId("fancynpcs", npcID);

        AuroraQuests.getInstance().getQuestManager()
                .progress(event.getPlayer(), TaskType.INTERACT_NPC, 1, Map.of("type", id));

        if (event.getInteractionType().equals(NpcInteractEvent.InteractionType.RIGHT_CLICK)) {
            var simpleID = new TypeId("fancynpcs", npcID);
            AuroraQuests.getInstance().getQuestManager()
                    .progress(event.getPlayer(), TaskType.INTERACT_NPC, 1, Map.of("type", simpleID));
        }
    }
}
