package gg.auroramc.quests.hooks.mythicmobs;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class MythicHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new MythicMobListener(), plugin);
        AuroraQuests.logger().info("Hooked into MythicMobs for KILL_MYTHIC_MOB task type");
    }
}
