package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Map;

public class MilkingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMilk(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Cow && event.getRightClicked() instanceof Goat) || (event.getPlayer().getInventory().getItemInMainHand()).getType() != Material.BUCKET) {
            return;
        }

        if (event.getPlayer().hasMetadata("NPC")) return;

        Player player = event.getPlayer();

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.MILK, 1,
                        Map.of("type", event.getRightClicked() instanceof Cow ? TypeId.fromDefault("cow") : TypeId.fromDefault("goat")));
    }
}
