package gg.auroramc.quests.menu;

import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.quests.api.quest.QuestPool;
import org.bukkit.entity.Player;

public class LevelMenu {
    private final Player player;
    private final QuestPool pool;

    public LevelMenu(Player player, QuestPool pool) {
        this.player = player;
        this.pool = pool;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var menu = new AuroraMenu(player, "Level Menu", 54, false);
        return menu;
    }
}
