package gg.auroramc.quests.hooks.auraskills;

import com.google.common.collect.Maps;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import gg.auroramc.aurora.api.reward.PermissionReward;
import gg.auroramc.aurora.api.reward.RewardCorrector;
import gg.auroramc.quests.AuroraQuests;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class AuraSkillsCorrector implements RewardCorrector {


    @Override
    public void correctRewards(Player player) {
        var manager = AuroraQuests.getInstance().getQuestManager();
        Map<Stat, Double> statMap = Maps.newHashMap();

        // Reset all stat modifiers first
        for (var stat : AuraSkillsApi.get().getGlobalRegistry().getStats()) {
            statMap.put(stat, 0.0);
        }

        // Gather new stat modifiers
        for (var pool : manager.getQuestPools()) {
            // Correct global quests
            if (pool.isGlobal()) {
                for (var quest : pool.getQuests()) {
                    if (!quest.isCompleted(player)) continue;

                    for (var reward : quest.getRewards().values()) {
                        if (reward instanceof AuraSkillsStatReward statReward) {
                            statMap.merge(statReward.getStat(), statReward.getValue(quest.getPlaceholders(player)), Double::sum);
                        }
                    }
                }
            }

            // Correct quest pool leveling
            if (!pool.hasLeveling()) return;
            var level = pool.getPlayerLevel(player);

            for (int i = 1; i < level + 1; i++) {
                var matcher = pool.getMatcherManager().getBestMatcher(i);
                if (matcher == null) continue;
                var placeholders = pool.getLevelPlaceholders(player, i);
                for (var reward : matcher.computeRewards(i)) {
                    if (reward instanceof AuraSkillsStatReward statReward) {
                        statMap.merge(statReward.getStat(), statReward.getValue(placeholders), Double::sum);
                    }
                }
            }
        }

        Bukkit.getGlobalRegionScheduler().run(AuroraQuests.getInstance(), (task) -> {
            for (var entry : statMap.entrySet()) {
                var statKey = AuraSkillsStatReward.getAURA_SKILLS_STAT() + entry.getKey().getId().toString();
                var user = AuraSkillsApi.get().getUser(player.getUniqueId());

                var oldModifier = user.getStatModifier(statKey);

                if (oldModifier == null) {
                    if (entry.getValue() > 0) {
                        user.addStatModifier(new StatModifier(statKey, entry.getKey(), entry.getValue()));
                    }
                } else if (entry.getValue() <= 0) {
                    user.removeStatModifier(statKey);
                } else if (entry.getValue() != oldModifier.value()) {
                    user.addStatModifier(new StatModifier(statKey, entry.getKey(), entry.getValue()));
                }
            }
        });
    }
}
