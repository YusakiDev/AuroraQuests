package gg.auroramc.quests.hooks.auraskills;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class AuraSkillsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new AuraSkillsListener(), plugin);
        AuroraQuests.logger().info("Hooked into AuraSkills for GAIN_AURASKILLS_XP task types");
    }
}
