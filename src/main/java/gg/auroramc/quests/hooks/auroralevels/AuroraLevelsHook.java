package gg.auroramc.quests.hooks.auroralevels;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class AuroraLevelsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new AuroraLevelsListener(), plugin);
        AuroraQuests.logger().info("Hooked into AuroraLevels for GAIN_AURORALEVELS_XP and GAIN_AURORALEVEL task types");
    }
}
