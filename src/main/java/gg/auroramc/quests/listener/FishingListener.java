package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class FishingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishCaught(PlayerFishEvent event) {
        PlayerFishEvent.State state = event.getState();
        if (state != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Entity entity = event.getCaught();
        if (!(entity instanceof Item caught)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) {
            return;
        }

        ItemStack item = caught.getItemStack();
        var amount = item.getAmount();

        var id = AuroraAPI.getItemManager().resolveId(item).toString();

        if (id.startsWith("minecraft") && item.hasItemMeta()) {
            if (item.getItemMeta().hasCustomModelData()) {
                // To support plugins that use custom model data on fishing loot and doesn't have an API otherwise
                id = id + ":" + item.getItemMeta().getCustomModelData();
            }
        }

        AuroraQuests.getInstance().getQuestManager()
                .progress(player, TaskType.FISH, amount, Map.of("type", id));
    }
}
