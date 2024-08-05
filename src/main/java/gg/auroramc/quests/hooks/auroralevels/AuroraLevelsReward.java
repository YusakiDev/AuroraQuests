package gg.auroramc.quests.hooks.auroralevels;

import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.reward.NumberReward;
import gg.auroramc.levels.api.AuroraLevelsProvider;
import org.bukkit.entity.Player;

import java.util.List;

public class AuroraLevelsReward extends NumberReward {
    @Override
    public void execute(Player player, long level, List<Placeholder<?>> placeholders) {
        AuroraLevelsProvider.getLeveler().addXpToPlayer(player, getValue(placeholders));
    }
}
