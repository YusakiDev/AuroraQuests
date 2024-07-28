package gg.auroramc.quests.hooks.citizens;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class CitizensListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onNPCRightClick(NPCRightClickEvent e) {
        var player = e.getClicker();
        var npc = e.getNPC();

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.INTERACT_NPC, 1, Map.of("type", String.valueOf(npc.getId())));
    }
}
