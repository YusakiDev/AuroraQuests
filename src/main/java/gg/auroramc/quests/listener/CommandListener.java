package gg.auroramc.quests.listener;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;

public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }


        String message = event.getMessage();
        if (!message.isEmpty()) {
            message = message.substring(1);
        }

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.RUN_COMMAND, 1, Map.of("type", message));
    }
}
