package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import gg.auroramc.quests.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class CraftListener implements Listener {
    private static int calculateCraftAmount(final CraftItemEvent event) {
        final ItemStack result = event.getRecipe().getResult();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        final ItemStack[] ingredients = event.getInventory().getMatrix();
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return InventoryUtils.calculateShiftCraftAmount(result, inventory, ingredients);
            case CONTROL_DROP:
                return InventoryUtils.calculateMaximumCraftAmount(result, ingredients);
            case NUMBER_KEY:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP:
                return InventoryUtils.calculateDropCraftAmount(result, event.getCursor());
            case LEFT:
            case RIGHT:
                return InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            default:
                return 0;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof final Player player) {
            var item = event.getRecipe().getResult();
            if (item.isEmpty()) return;
            var amount = calculateCraftAmount(event);
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.CRAFT, amount, Map.of("type", AuroraAPI.getItemManager().resolveId(item)));
        }
    }
}
