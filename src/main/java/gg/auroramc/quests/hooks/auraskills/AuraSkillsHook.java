package gg.auroramc.quests.hooks.auraskills;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;

public class AuraSkillsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        Bukkit.getPluginManager().registerEvents(new AuraSkillsListener(), plugin);

        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("auraskills_stat"), AuraSkillsStatReward.class);

        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("auraskills_xp"), AuraSkillsXpReward.class);

        plugin.getQuestManager().getRewardAutoCorrector()
                .registerCorrector(NamespacedId.fromDefault("auraskills_stat"), new AuraSkillsCorrector());

        AuroraQuests.logger().info("Hooked into AuraSkills for GAIN_AURASKILLS_XP task types and for auraskills_stat/auraskills_xp rewards");
    }
}
