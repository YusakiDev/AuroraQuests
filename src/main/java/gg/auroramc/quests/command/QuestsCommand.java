package gg.auroramc.quests.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.menu.MainMenu;
import gg.auroramc.quests.menu.PoolMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%questsAlias")
public class QuestsCommand extends BaseCommand {
    private final AuroraQuests plugin;

    public QuestsCommand(AuroraQuests plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Opens the quests menu")
    @CommandPermission("aurora.quests.use")
    public void onMenu(Player player) {
        if (!AuroraAPI.getUser(player.getUniqueId()).isLoaded()) {
            Chat.sendMessage(player, plugin.getConfigManager().getMessageConfig().getDataNotLoadedYetSelf());
            return;
        }
        new MainMenu(player).open();
    }

    @Subcommand("reload")
    @Description("Reloads the plugin configs and applies reward auto correctors to players")
    @CommandPermission("aurora.quests.admin.reload")
    public void onReload(CommandSender sender) {
        plugin.reload();
        Chat.sendMessage(sender, plugin.getConfigManager().getMessageConfig().getReloaded());
    }

    @Subcommand("open")
    @Description("Opens the quest menu for another player in a specific pool")
    @CommandCompletion("@players @pools true|false")
    @CommandPermission("aurora.collections.admin.open")
    public void onOpenMenu(CommandSender sender, @Flags("other") Player target, @Default("none") String poolId, @Default("false") Boolean silent) {
        if (poolId.equals("none")) {
            new MainMenu(target).open();
        } else {
            var pool = plugin.getQuestManager().getQuestPool(poolId);
            if(pool== null) {
                Chat.sendMessage(sender, plugin.getConfigManager().getMessageConfig().getPoolNotFound());
                return;
            }
            new PoolMenu(target, pool).open();
        }

        if (!silent) {
            Chat.sendMessage(sender, plugin.getConfigManager().getMessageConfig().getMenuOpened(), Placeholder.of("{player}", target.getName()));
        }
    }
}
