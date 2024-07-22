package gg.auroramc.quests.placeholder;

import gg.auroramc.aurora.api.placeholder.PlaceholderHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestPlaceholderHandler implements PlaceholderHandler {
    @Override
    public String getIdentifier() {
        return "quests";
    }

    @Override
    public String onPlaceholderRequest(Player player, String[] strings) {
        return null;
    }

    @Override
    public List<String> getPatterns() {
        return List.of();
    }
}
