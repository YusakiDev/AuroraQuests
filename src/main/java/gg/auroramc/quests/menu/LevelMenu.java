package gg.auroramc.quests.menu;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.aurora.api.menu.ItemBuilder;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.message.Text;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.QuestPool;
import gg.auroramc.quests.util.RomanNumber;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelMenu {
    private final Player player;
    private final QuestPool pool;
    private int page = 0;

    public LevelMenu(Player player, QuestPool pool) {
        this.player = player;
        this.pool = pool;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var cf = pool.getConfig().getLeveling();
        var cm = cf.getMenu();
        var mmc = AuroraQuests.getInstance().getConfigManager().getMainMenuConfig();

        var menu = new AuroraMenu(player, cm.getTitle(), cm.getRows() * 9, false, Placeholder.of("{name}", pool.getConfig().getName()));

        if (cm.getFiller().getEnabled()) {
            menu.addFiller(ItemBuilder.of(cm.getFiller().getItem()).toItemStack(player));
        } else {
            menu.addFiller(ItemBuilder.filler(Material.AIR));
        }

        if (cm.getHasBackButton()) {
            menu.addItem(ItemBuilder.back(mmc.getItems().get("back").merge(cm.getItems().get("back"))).build(player), (e) -> {
                new PoolMenu(player, pool).open();
            });
        }

        if (cm.getHasCloseButton()) {
            menu.addItem(ItemBuilder.close(mmc.getItems().get("close").merge(cm.getItems().get("close"))).build(player), (e) -> {
                player.closeInventory();
            });
        }

        var level = pool.getPlayerLevel(player);

        List<Placeholder<?>> placeholders = List.of(
                Placeholder.of("{name}", pool.getConfig().getName()),
                Placeholder.of("{level}", AuroraAPI.formatNumber(level)),
                Placeholder.of("{level_roman}", RomanNumber.toRoman(level)),
                Placeholder.of("{level_raw}", level)
        );

        for (var customItem : cm.getCustomItems().values()) {
            menu.addItem(ItemBuilder.of(customItem).placeholder(placeholders).build(player));
        }

        var requirements = getPage(page, cm.getDisplayArea().size());

        for (int i = 0; i < cm.getDisplayArea().size(); i++) {
            var slot = cm.getDisplayArea().get(i);
            if (requirements.size() <= i) {
                break;
            }

            var requirement = requirements.get(i);
            var rLevel = cf.getRequirements().indexOf(requirement) + 1;
            var completed = level >= rLevel;
            var itemConfig = completed
                    ? mmc.getItems().get("completed-level").merge(cm.getItems().get("completed-level"))
                    : mmc.getItems().get("locked-level").merge(cm.getItems().get("locked-level"));


            var rewards = pool.getMatcherManager().getBestMatcher(level).computeRewards(level);

            var currentProgress = Math.min(pool.getCompletedQuestCount(player), requirement);
            var bar = mmc.getProgressBar();
            var pcs = bar.getLength();
            var completedPercent = Math.min((double) currentProgress / requirement, 1);
            var completedPcs = ((Double) Math.floor(pcs * completedPercent)).intValue();
            var remainingPcs = pcs - completedPcs;

            var lore = new ArrayList<String>();

            List<Placeholder<?>> rPlaceholders = List.of(
                    Placeholder.of("{player}", player.getName()),
                    Placeholder.of("{pool}", pool.getConfig().getName()),
                    Placeholder.of("{pool_id}", pool.getId()),
                    Placeholder.of("{level}", AuroraAPI.formatNumber(rLevel)),
                    Placeholder.of("{level_roman}", RomanNumber.toRoman(rLevel)),
                    Placeholder.of("{level_raw}", rLevel),
                    Placeholder.of("{current}", AuroraAPI.formatNumber(currentProgress)),
                    Placeholder.of("{required}", AuroraAPI.formatNumber(requirement)),
                    Placeholder.of("{progressbar}", bar.getFilledCharacter().repeat(completedPcs) + bar.getUnfilledCharacter().repeat(remainingPcs) + "&r"),
                    Placeholder.of("{progress_percent}", Math.round(completedPercent * 100))
            );

            for (var line : itemConfig.getLore()) {
                if (line.equals("component:rewards")) {
                    var display = mmc.getDisplayComponents().get("rewards");

                    if (!rewards.isEmpty()) {
                        lore.add(display.getTitle());
                    }
                    for (var reward : rewards) {
                        lore.add(display.getLine().replace("{reward}", reward.getDisplay(player, rPlaceholders)));
                    }
                } else {
                    lore.add(line);
                }
            }

            var builder = ItemBuilder.of(itemConfig).slot(slot)
                    .loreCompute(() -> lore.stream().map(l -> Text.component(player, l, rPlaceholders)).toList())
                    .placeholder(rPlaceholders);

            if (cm.getAllowItemAmounts()) {
                builder.amount(rLevel);
            }

            menu.addItem(builder.build(player));

        }

        // Pagination
        if (cf.getRequirements().size() > cm.getDisplayArea().size()) {
            var pageCount = getTotalPageCount(cm.getDisplayArea().size());

            List<Placeholder<?>> pl = List.of(Placeholder.of("{current}", page + 1), Placeholder.of("{max}", pageCount + 1));

            menu.addItem(ItemBuilder.of(mmc.getItems().get("previous-page").merge(cm.getItems().get("previous-page")))
                    .placeholder(pl).build(player), (e) -> {
                if (page > 0) {
                    page--;
                    createMenu().open();
                }
            });

            menu.addItem(ItemBuilder.of(mmc.getItems().get("current-page").merge(cm.getItems().get("current-page")))
                    .placeholder(pl).build(player));

            menu.addItem(ItemBuilder.of(mmc.getItems().get("next-page").merge(cm.getItems().get("next-page")))
                    .placeholder(pl).build(player), (e) -> {
                if (page < pageCount) {
                    page++;
                    createMenu().open();
                }
            });
        }

        return menu;
    }

    private List<Integer> getPage(int page, int pageSize) {
        var requirements = pool.getConfig().getLeveling().getRequirements();
        return requirements.stream().skip((long) page * pageSize).limit(pageSize).toList();
    }

    private int getTotalPageCount(int pageSize) {
        var requirements = pool.getConfig().getLeveling().getRequirements();
        return (int) Math.ceil((double) requirements.size() / pageSize) - 1;
    }
}
