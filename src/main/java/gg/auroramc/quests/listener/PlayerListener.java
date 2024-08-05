package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.event.QuestCompletedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {
    private final AuroraQuests plugin;

    public PlayerListener(AuroraQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLoaded(AuroraUserLoadedEvent event) {
        var player = event.getUser().getPlayer();
        if (player == null) return;

        if (event.isAsynchronous()) {
            roll(player);
            plugin.getQuestManager().getRewardAutoCorrector().correctRewards(player);
        } else {
            CompletableFuture.runAsync(() -> {
                roll(player);
                plugin.getQuestManager().getRewardAutoCorrector().correctRewards(player);
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getQuestManager().handlePlayerQuit(event.getPlayer().getUniqueId());
    }

    private void roll(Player player) {
        plugin.getQuestManager().tryStartGlobalQuests(player);
        var pools = plugin.getQuestManager().rollQuestsIfNecessary(player);

        if (pools.isEmpty()) return;

        var msg = plugin.getConfigManager().getMessageConfig().getReRolledTarget();
        Chat.sendMessage(player, msg, Placeholder.of("{pool}", String.join(", ", pools.stream().map(p -> p.getConfig().getName()).toList())));
    }

    @EventHandler
    public void onQuestComplete(QuestCompletedEvent event) {
        plugin.getQuestManager().tryStartGlobalQuests(event.getPlayer());
    }
}
