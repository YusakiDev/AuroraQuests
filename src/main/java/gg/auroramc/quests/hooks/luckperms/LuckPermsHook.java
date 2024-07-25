package gg.auroramc.quests.hooks.luckperms;

import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;

public class LuckPermsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        var lp = LuckPermsProvider.get();

        lp.getEventBus().subscribe(UserDataRecalculateEvent.class, (event) -> {
            var player = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (player != null) plugin.getQuestManager().tryStartGlobalQuests(player);
        });
    }
}
