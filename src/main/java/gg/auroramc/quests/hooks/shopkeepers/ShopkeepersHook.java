package gg.auroramc.quests.hooks.shopkeepers;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class ShopkeepersHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new ShopkeepersListener(), plugin);
        AuroraQuests.logger().info("Hooked into Shopkeepers for INTERACT_SHOPKEEPER and TRADE_SHOPKEEPER task types");
    }
}
