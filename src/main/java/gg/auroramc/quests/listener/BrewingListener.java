package gg.auroramc.quests.listener;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BrewingListener implements Listener {
    private final HashMap<Location, UUID> brewingStands = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();
        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        final InventoryHolder holder = inventory.getHolder();
        if (holder == null) {
            return;
        }

        brewingStands.put(inventory.getLocation(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        UUID uuid;
        if ((uuid = brewingStands.get(event.getBlock().getLocation())) == null) {
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }


        var potionKeys = new ArrayList<String>();

        for (ItemStack item : event.getResults()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                for (PotionEffect effect : meta.getCustomEffects()) {
                    var type = effect.getType();
                    int level = effect.getAmplifier() + 1; // Amplifier starts at 0 for level I potions
                    potionKeys.add(type + ";" + level);

                }
            }
        }


        for (var key : potionKeys) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BREW, 1, Map.of("type", key));
        }
    }
}
