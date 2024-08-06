package gg.auroramc.quests.hooks.luckperms;

import gg.auroramc.aurora.api.reward.PermissionReward;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.hooks.Hook;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;

public class LuckPermsHook implements Hook {
    @Override
    public void hook(AuroraQuests plugin) {
        plugin.getQuestManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("permission"), PermissionReward.class);

        plugin.getQuestManager().getRewardAutoCorrector()
                .registerCorrector(NamespacedId.fromDefault("permission"), new PermissionCorrector());

        var lp = LuckPermsProvider.get();

        lp.getEventBus().subscribe(UserDataRecalculateEvent.class, (event) -> {
            var player = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (player != null) {
                plugin.getQuestManager().tryUnlockQuestPools(player);
                plugin.getQuestManager().tryStartGlobalQuests(player);
            }
        });

        AuroraQuests.logger().info("Hooked into LuckPerms for permission rewards and for permission start requirements");
    }
}
