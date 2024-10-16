package gg.auroramc.quests.hooks.auraskills;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class AuraSkillsHook implements Hook {
    private AuraSkillsCorrector corrector;

    @Override
    public void hook(AuroraQuests plugin) {
        this.corrector = new AuraSkillsCorrector();

        Bukkit.getPluginManager().registerEvents(new AuraSkillsListener(this), plugin);

        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("auraskills_stat"), AuraSkillsStatReward.class);

        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("auraskills_xp"), AuraSkillsXpReward.class);

        plugin.getQuestManager().getRewardAutoCorrector()
                .registerCorrector(NamespacedId.fromDefault("auraskills_stat"), this.corrector);

        AuroraQuests.logger().info("Hooked into AuraSkills for GAIN_AURASKILLS_XP task types and for auraskills_stat/auraskills_xp rewards");
    }
}
