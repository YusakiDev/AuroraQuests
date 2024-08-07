package gg.auroramc.quests.hooks.economyshopgui;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import me.gypopo.economyshopgui.api.events.PostTransactionEvent;
import me.gypopo.economyshopgui.util.Transaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;

public class EconomyShopGUIListener implements Listener {
    private final Set<Transaction.Result> successResults = Set.of(
            Transaction.Result.SUCCESS, Transaction.Result.SUCCESS_COMMANDS_EXECUTED,
            Transaction.Result.NOT_ALL_ITEMS_ADDED
    );
    private final Set<Transaction.Type> buyTypes = Set.of(
            Transaction.Type.BUY_SCREEN, Transaction.Type.BUY_STACKS_SCREEN, Transaction.Type.QUICK_BUY,
            Transaction.Type.SHOPSTAND_BUY_SCREEN
    );
    private final Set<Transaction.Type> sellTypes = Set.of(
            Transaction.Type.SELL_GUI_SCREEN, Transaction.Type.SELL_SCREEN, Transaction.Type.SELL_ALL_SCREEN,
            Transaction.Type.SELL_ALL_COMMAND, Transaction.Type.QUICK_SELL, Transaction.Type.AUTO_SELL_CHEST,
            Transaction.Type.SHOPSTAND_SELL_SCREEN
    );


    @EventHandler(priority = EventPriority.MONITOR)
    public void onTransaction(PostTransactionEvent e) {
        if (!successResults.contains(e.getTransactionResult())) {
            return;
        }

        var manager = AuroraQuests.getInstance().getQuestManager();
        var price = e.getPrice();

        if (sellTypes.contains(e.getTransactionType())) {
            manager.progress(e.getPlayer(), TaskType.SELL_WORTH, price, null);
        } else if (buyTypes.contains(e.getTransactionType())) {
            manager.progress(e.getPlayer(), TaskType.BUY_WORTH, price, null);
        }

        if (sellTypes.contains(e.getTransactionType()) && !e.getItems().isEmpty()) {
            for (var entry : e.getItems().entrySet()) {
                var item = entry.getKey().getItemToGive();
                var amount = entry.getValue();

                if (item != null) {
                    var id = AuroraAPI.getItemManager().resolveId(item);
                    if (id != null) {
                        manager.progress(e.getPlayer(), TaskType.SELL, amount, Map.of("type", id));
                    }
                }
            }
        } else if (sellTypes.contains(e.getTransactionType())) {
            var item = e.getShopItem().getItemToGive();
            var amount = e.getAmount();
            if (item != null) {
                var id = AuroraAPI.getItemManager().resolveId(item);
                if (id != null) {
                    manager.progress(e.getPlayer(), TaskType.SELL, amount, Map.of("type", id));
                }
            }
        } else if (buyTypes.contains(e.getTransactionType())) {
            var item = e.getShopItem().getItemToGive();
            var amount = e.getAmount();
            if (item != null) {
                var id = AuroraAPI.getItemManager().resolveId(item);
                if (id != null) {
                    manager.progress(e.getPlayer(), TaskType.BUY, amount, Map.of("type", id));
                }
            }
        }
    }
}
