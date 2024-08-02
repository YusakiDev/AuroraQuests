package gg.auroramc.quests.menu;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.aurora.api.menu.ItemBuilder;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class MainMenu {
    private final Player player;
    private int page = 1;
    private int maxPage = 1;

    public MainMenu(Player player) {
        this.player = player;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var config = AuroraQuests.getInstance().getConfigManager().getMainMenuConfig();

        var menu = new AuroraMenu(player, config.getTitle(), 54, false);

        if (config.getFiller().getEnabled()) {
            menu.addFiller(ItemBuilder.of(config.getFiller().getItem()).toItemStack(player));
        } else {
            menu.addFiller(ItemBuilder.filler(Material.AIR));
        }

        menu.addItem(ItemBuilder.close(config.getItems().get("close")).build(player), (e) -> {
            player.closeInventory();
        });

        var pools = AuroraQuests.getInstance().getQuestManager().getQuestPools();
        var maybeInt = pools.stream().filter(pool -> pool.getConfig().getMenuItem().getShowInMainMenu()).mapToInt(pool -> pool.getConfig().getMenuItem().getPage()).max();
        if (maybeInt.isPresent()) {
            maxPage = maybeInt.getAsInt();
        }

        for (var pool : pools) {
            var mi = pool.getConfig().getMenuItem();
            if (!mi.getShowInMainMenu()) continue;
            if (mi.getPage() != page) continue;

            // TODO: add leaderboard placeholders
            menu.addItem(ItemBuilder.of(mi.getItem())
                    .placeholder(Placeholder.of("{name}", pool.getConfig().getName()))
                    .placeholder(Placeholder.of("{total_completed}", AuroraAPI.formatNumber(pool.getCompletedQuestCount(player))))
                    .build(player), (e) -> {
                new PoolMenu(player, pool).open();
            });
        }

        for (var customItem : config.getCustomItems().values()) {
            menu.addItem(ItemBuilder.of(customItem).build(player));
        }

        if (maxPage > 1) {
            // Add pagination items
            List<Placeholder<?>> placeholders = List.of(
                    Placeholder.of("{curent}", page),
                    Placeholder.of("{total}", maxPage)
            );

            menu.addItem(ItemBuilder.of(config.getItems().get("previous-page")).placeholder(placeholders).build(player), (e) -> {
                if (page > 1) {
                    page--;
                    createMenu().open();
                }
            });

            menu.addItem(ItemBuilder.of(config.getItems().get("current-page")).placeholder(placeholders).build(player));

            menu.addItem(ItemBuilder.of(config.getItems().get("next-page")).placeholder(placeholders).build(player), (e) -> {
                if (page < maxPage) {
                    page++;
                    createMenu().open();
                }
            });
        }

        return menu;
    }
}
