package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.util.BukkitPotionType;
import gg.auroramc.aurora.api.util.Version;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


public class BrewingListener implements Listener {
    private NamespacedKey potionKey = new NamespacedKey(AuroraQuests.getInstance(), "counted");
    private final Map<Location, Player> brewingStands = new HashMap<>();

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

        if (event.getPlayer() instanceof Player player) {
            brewingStands.put(inventory.getLocation(), player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        var player = brewingStands.remove(event.getBlock().getLocation());

        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        for (ItemStack item : event.getResults()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta meta) {
                if (meta.getPersistentDataContainer().has(potionKey, PersistentDataType.BYTE)) {
                    continue;
                }

                var type = new BukkitPotionType(meta);
                var typeString = type.getType().name().toLowerCase(Locale.ROOT);

                if (!Version.isAtLeastVersion(20, 2)) {
                    if (type.isExtended()) {
                        typeString = "long_" + typeString;
                    } else if (type.isUpgraded()) {
                        typeString = "strong_" + typeString;
                    }
                }

                AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.BREW, 1, Map.of("type", typeString));

                meta.getPersistentDataContainer().set(potionKey, PersistentDataType.BYTE, (byte) 1);
                item.setItemMeta(meta);
            }
        }
    }
}
