package gg.auroramc.quests.hooks.shopkeepers;

import com.nisovin.shopkeepers.api.events.ShopkeeperOpenUIEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class ShopkeepersListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onOpenShop(ShopkeeperOpenUIEvent e) {
        var shopkeeper = e.getShopkeeper();
        var player = e.getPlayer();

        AuroraQuests.getInstance().getQuestManager().progress(player, TaskType.INTERACT_SHOPKEEPER, 1, Map.of("type", String.valueOf(shopkeeper.getId())));
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopkeeperTrade(ShopkeeperTradeEvent e) {
        var item = e.getTradingRecipe().getResultItem().copy();
        var quantity = item.getAmount();

        var id = AuroraAPI.getItemManager().resolveId(item);

        AuroraQuests.getInstance().getQuestManager()
                .progress(e.getPlayer(), TaskType.TRADE_SHOPKEEPER, quantity, Map.of("type", id));
    }
}
