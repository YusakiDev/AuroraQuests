package gg.auroramc.quests.listener;

import com.google.common.math.IntMath;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.util.Version;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmeltingListener implements Listener {

    public boolean isOffHandSwap(ClickType clickType) {
        return clickType == ClickType.SWAP_OFFHAND;
    }

    public boolean isOffHandEmpty(Player player) {
        return player.getInventory().getItemInOffHand().getAmount() == 0;
    }

    public int getAvailableSpace(Player player, ItemStack newItemStack) {
        int availableSpace = 0;
        PlayerInventory inventory = player.getInventory();
        HashMap<Integer, ? extends ItemStack> itemStacksWithSameMaterial = inventory.all(newItemStack.getType());
        for (ItemStack existingItemStack : itemStacksWithSameMaterial.values()) {
            if (newItemStack.isSimilar(existingItemStack)) {
                availableSpace += (newItemStack.getMaxStackSize() - existingItemStack.getAmount());
            }
        }

        for (ItemStack existingItemStack : inventory.getStorageContents()) {
            if (existingItemStack == null) {
                availableSpace += newItemStack.getMaxStackSize();
            }
        }

        return availableSpace;
    }

    private final Set<String> modes = Set.of("smoker", "blast_furnace", "furnace");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof FurnaceInventory) || event.getRawSlot() != 2
                || (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                || event.getAction() == InventoryAction.NOTHING
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR && event.getClick() == ClickType.DOUBLE_CLICK && event.getCursor().getType() != Material.AIR && ((event.getCursor().getAmount() + event.getCurrentItem().getAmount() > event.getCursor().getMaxStackSize()) || event.getCursor().getType() != event.getCurrentItem().getType()) // does not apply to crafting tables lol
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD && event.getClick() == ClickType.NUMBER_KEY
                || (Version.isAtLeastVersion(20, 6) && event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY)
                || event.getAction() == InventoryAction.DROP_ONE_SLOT && event.getClick() == ClickType.DROP && event.getCursor().getType() != Material.AIR
                || event.getAction() == InventoryAction.DROP_ALL_SLOT && event.getClick() == ClickType.CONTROL_DROP && event.getCursor().getType() != Material.AIR
                || event.getAction() == InventoryAction.UNKNOWN && event.getClick() == ClickType.UNKNOWN // for better ViaVersion support
                || !(event.getWhoClicked() instanceof Player player)
                || isOffHandSwap(event.getClick()) && !isOffHandEmpty(player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        int eventAmount;
        if (event.getAction() == InventoryAction.DROP_ONE_SLOT) {
            eventAmount = 1;
        } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
            eventAmount = IntMath.divide(item.getAmount(), 2, RoundingMode.CEILING);
        } else {
            eventAmount = item.getAmount();
            if (event.isShiftClick() && event.getClick() != ClickType.CONTROL_DROP) {
                eventAmount = Math.min(eventAmount, getAvailableSpace(player, item));
                if (eventAmount == 0) {
                    return;
                }
            }
        }


        final InventoryType inventoryType = event.getInventory().getType();

        var id = AuroraAPI.getItemManager().resolveId(item);

        if (modes.contains(inventoryType.name().toLowerCase())) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.SMELT, eventAmount, Map.of("type", id));
        }
    }
}
