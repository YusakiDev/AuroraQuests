package gg.auroramc.quests.listener;

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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class CraftListener implements Listener {
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        //noinspection DuplicatedCode
        if ((event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                || event.getAction() == InventoryAction.NOTHING
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
        int eventAmount = item.getAmount();

        if (event.isShiftClick() && event.getClick() != ClickType.CONTROL_DROP) {
            int maxAmount = event.getInventory().getMaxStackSize();
            ItemStack[] matrix = event.getInventory().getMatrix();
            for (ItemStack itemStack : matrix) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    int itemStackAmount = itemStack.getAmount();
                    if (itemStackAmount < maxAmount && itemStackAmount > 0) {
                        maxAmount = itemStackAmount;
                    }
                }
            }
            eventAmount *= maxAmount;
            eventAmount = Math.min(eventAmount, getAvailableSpace(player, item));
            if (eventAmount == 0) {
                return;
            }
        }

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.CRAFT, eventAmount, Map.of("type", AuroraAPI.getItemManager().resolveId(item)));
    }
}
