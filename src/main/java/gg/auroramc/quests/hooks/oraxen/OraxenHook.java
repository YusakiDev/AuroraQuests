package gg.auroramc.quests.hooks.oraxen;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class OraxenHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        if (Bukkit.getPluginManager().getPlugin("Oraxen").getDescription().getVersion().startsWith("1")) {
            Bukkit.getPluginManager().registerEvents(new OraxenListenerV1(), plugin);
            AuroraQuests.logger().info("Hooked into Oraxen for BLOCK_BREAK task progression.");
        }
    }
}
