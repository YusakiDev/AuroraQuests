package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.quests.AuroraQuests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {
    private final AuroraQuests plugin;

    public PlayerListener(AuroraQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLoaded(AuroraUserLoadedEvent event) {
        if(event.getUser().getPlayer() == null) return;
        plugin.getQuestManager().rollQuestsIfNecessary(event.getUser().getPlayer());
    }
}
