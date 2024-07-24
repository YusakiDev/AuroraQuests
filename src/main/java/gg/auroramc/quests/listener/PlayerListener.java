package gg.auroramc.quests.listener;

import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLoaded(AuroraUserLoadedEvent event) {
        if(event.getUser().getPlayer() == null) return;
        // Roll quests for every pool if the player has no active quests in them
    }
}
