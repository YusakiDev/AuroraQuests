package gg.auroramc.quests.menu;

import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.quests.api.quest.Quest;
import org.bukkit.entity.Player;

public class LevelMenu {
    private final Player player;
    private final Quest quest;

    public LevelMenu(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var menu = new AuroraMenu(player, "Level Menu", 54, false);
        return menu;
    }
}
