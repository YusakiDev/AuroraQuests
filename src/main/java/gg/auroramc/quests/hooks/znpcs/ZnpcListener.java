package gg.auroramc.quests.hooks.znpcs;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.event.NPCInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class ZnpcListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onNPCRightClick(NPCInteractEvent e) {

        io.github.gonalez.znpcs.npc.NPC npc = e.getNpc();
        Player player = e.getPlayer();

        var id = new TypeId("znpcs", String.valueOf(e.getNpc().getNpcPojo().getId()));
        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.INTERACT_NPC, 1, Map.of("type", id));
    }
}
