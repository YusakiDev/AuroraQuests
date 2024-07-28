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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Map;

public class SmithingListener implements Listener {
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
    public void onSmithItem(SmithItemEvent event) {
        //noinspection DuplicatedCode
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR
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
            eventAmount *= getSmithItemMultiplier(event);
            eventAmount = Math.min(eventAmount, getAvailableSpace(player, item));
            if (eventAmount == 0) {
                return;
            }
        }

        final String recipeType = getSmithMode(event);

        if (recipeType.equals("trim")) {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.SMITH_TRIM, eventAmount, Map.of("type", AuroraAPI.getItemManager().resolveId(item)));
        } else {
            AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.SMITH_TRANSFORM, eventAmount, Map.of("type", AuroraAPI.getItemManager().resolveId(item)));
        }
    }

    private int getSmithItemMultiplier(SmithItemEvent event) {
        int min = -1;
        for (ItemStack item : getSmithItems(event)) {
            if (item == null) {
                continue;
            }
            int amount = item.getAmount();
            if ((min < 0 && amount > 0) || (min > 0 && amount < min)) {
                min = amount;
            }
        }
        return Math.max(min, 1);
    }

    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral(),
                event.getInventory().getInputTemplate()
        };
    }

    public String getSmithMode(SmithItemEvent event) {
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe instanceof SmithingTransformRecipe) {
            return "transform";
        } else if (recipe instanceof SmithingTrimRecipe) {
            return "trim";
        } else {
            return null;
        }
    }
}
