package gg.auroramc.quests.hooks.superiorskyblock;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.*;
import com.bgsoftware.superiorskyblock.api.island.Island;
import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.quest.TaskType;
import gg.auroramc.quests.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class SuperiorSkyblockHook implements Hook, Listener {
    private AuroraQuests plugin;

    @Override
    public void hook(AuroraQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandCreate(IslandCreateEvent event) {
        plugin.getQuestManager().progress(event.getPlayer().asPlayer(), TaskType.JOIN_ISLAND, 1, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandJoin(IslandJoinEvent event) {
        plugin.getQuestManager().progress(event.getPlayer().asPlayer(), TaskType.JOIN_ISLAND, 1, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandWorthCalc(IslandWorthCalculatedEvent event) {
        var worth = event.getWorth().doubleValue();
        var level = event.getLevel().doubleValue();

        updateIslandMembers(worth, level, event.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandWorthCalc(IslandWorthUpdateEvent event) {
        var worth = event.getNewWorth().doubleValue();
        var level = event.getNewLevel().doubleValue();

        updateIslandMembers(worth, level, event.getIsland());
    }

    @EventHandler
    public void onUserLoaded(AuroraUserLoadedEvent event) {
        Bukkit.getGlobalRegionScheduler().runDelayed(AuroraQuests.getInstance(), (task) -> {
            var player = event.getUser().getPlayer();
            if (player == null || !player.isOnline()) return;

            var island = SuperiorSkyblockAPI.getPlayer(player).getIsland();

            if (island != null) {
                updateIslandMembers(
                        island.getWorth().doubleValue(),
                        island.getIslandLevel().doubleValue(),
                        island
                );
                updateIslandUpgrades(island);
            }
        }, 100);
    }

    private void updateIslandMembers(double worth, double level, Island island) {
        for (var sPlayer : island.getIslandMembers(true)) {
            var player = sPlayer.asPlayer();
            if (player != null) {
                plugin.getQuestManager().setProgress(player, TaskType.REACH_ISLAND_WORTH, worth, null);
                plugin.getQuestManager().setProgress(player, TaskType.REACH_ISLAND_LEVEL, level, null);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandUpgrade(IslandUpgradeEvent event) {
        var island = event.getIsland();
        for (var sPlayer : island.getIslandMembers(true)) {
            var player = sPlayer.asPlayer();
            if (player != null) {
                plugin.getQuestManager().setProgress(
                        player,
                        TaskType.UPGRADE_ISLAND,
                        event.getUpgradeLevel().getLevel(),
                        Map.of("type", event.getUpgrade().getName())
                );
            }
        }
    }

    private void updateIslandUpgrades(Island island) {
        for (var upgrade : island.getUpgrades().entrySet()) {
            for (var sPlayer : island.getIslandMembers(true)) {
                var player = sPlayer.asPlayer();
                if (player != null) {
                    plugin.getQuestManager().setProgress(
                            player,
                            TaskType.UPGRADE_ISLAND,
                            upgrade.getValue(),
                            Map.of("type", upgrade.getKey())
                    );
                }
            }
        }
    }
}
