package gg.auroramc.quests.menu;

import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.aurora.api.menu.ItemBuilder;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.QuestPool;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PoolMenu {
    private final Player player;
    private final QuestPool pool;

    public PoolMenu(Player player, QuestPool pool) {
        this.player = player;
        this.pool = pool;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var config = pool.getConfig();
        var mc = config.getMenu();
        var mmc = AuroraQuests.getInstance().getConfigManager().getMainMenuConfig();

        var menu = new AuroraMenu(player, mc.getTitle(), 54, false, Placeholder.of("{name}", config.getName()));

        if (config.getMenu().getFiller().getEnabled()) {
            menu.addFiller(ItemBuilder.of(mc.getFiller().getItem()).toItemStack(player));
        } else {
            menu.addFiller(ItemBuilder.filler(Material.AIR));
        }

        if(mc.getHasCloseButton()) {
            var closeConfig = mmc.getItems().get("close").merge(mc.getItems().get("close"));

            menu.addItem(ItemBuilder.close(closeConfig).build(player), (e) -> {
                player.closeInventory();
            });
        }

        if(mc.getHasBackButton()) {
            var backConfig = mmc.getItems().get("back").merge(mc.getItems().get("back"));

            menu.addItem(ItemBuilder.back(backConfig).build(player), (e) -> {
                new MainMenu(player).open();
            });
        }


        return menu;
    }
}
