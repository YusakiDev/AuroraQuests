package gg.auroramc.quests.hooks.shopguiplus;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import gg.auroramc.quests.hooks.Hook;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.ShopManager;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class ShopGUIPlusHook implements Hook, Listener {
    @Override
    public void hook(AuroraQuests plugin) {
        AuroraQuests.logger().info("Hooked into ShopGUIPlus for BUY_WORTH and SELL_WORTH task progression.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPostTransaction(ShopPostTransactionEvent event) {
        var res = event.getResult();

        if (res.getResult() != ShopTransactionResult.ShopTransactionResultType.SUCCESS) return;
        if (res.getPlayer() == null) return;

        var price = res.getPrice();
        var manager = AuroraQuests.getInstance().getQuestManager();
        var item = res.getShopItem().getItem();
        var id = item != null ? AuroraAPI.getItemManager().resolveId(item) : null;

        if (res.getShopAction() == ShopManager.ShopAction.BUY) {
            if (id != null) {
                manager.progress(res.getPlayer(), TaskType.BUY_WORTH, price, Map.of("type", id));
                manager.progress(res.getPlayer(), TaskType.BUY, res.getAmount(), Map.of("type", id));
            } else {
                manager.progress(res.getPlayer(), TaskType.BUY_WORTH, price, null);
            }
        } else {
            if (id != null) {
                manager.progress(res.getPlayer(), TaskType.SELL_WORTH, price, Map.of("type", id));
                manager.progress(res.getPlayer(), TaskType.SELL, res.getAmount(), Map.of("type", id));
            } else {
                manager.progress(res.getPlayer(), TaskType.SELL_WORTH, price, null);
            }
        }
    }
}
