package gg.auroramc.quests.hooks.auroralevels;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class AuroraLevelsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new AuroraLevelsListener(), plugin);

        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("levels_xp"), AuroraLevelsReward.class);

        AuroraQuests.logger().info("Hooked into AuroraLevels for GAIN_AURORA_XP and GAIN_AURORA_LEVEL task types and for levels_xp reward");
    }
}
