package gg.auroramc.quests.api.quest;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.config.quest.TaskConfig;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record Task(QuestPool pool, Quest holder, TaskConfig config, String id) {
    public void progress(Player player, double count, Map<String, Object> params) {
        if (!TaskManager.getEvaluator(config.getTask()).evaluate(player, config, params)) return;

        AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class)
                .progress(pool.getId(), holder.getId(), id, count);
    }

    public void setProgress(Player player, double count, Map<String, Object> params) {
        if (!TaskManager.getEvaluator(config.getTask()).evaluate(player, config, params)) return;

        AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class)
                .setProgress(pool.getId(), holder.getId(), id, count);
    }

    public CompletableFuture<Void> tryTakeItems(Player player) {
        var future = new CompletableFuture<Void>();

        if (!TaskManager.getEvaluator(config.getTask()).evaluate(player, config, Map.of())) {
            future.complete(null);
            return future;
        }

        var itemId = config.getArgs().getString("item");
        final var currentAmount = (int) AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class).getProgression(pool.getId(), holder.getId(), id);
        final var requiredAmount = config.getArgs().getInt("amount", 1);
        final var remainingAmount = requiredAmount - currentAmount;

        if (itemId == null || remainingAmount <= 0) {
            future.complete(null);
            return future;
        }

        final var typeId = TypeId.fromString(itemId);

        player.getScheduler().run(AuroraQuests.getInstance(), (st) -> {
            var amountNeeded = remainingAmount;

            for (var invItem : player.getInventory().getContents()) {
                if (invItem == null) continue;

                if (AuroraAPI.getItemManager().resolveId(invItem).equals(typeId)) {
                    var amount = invItem.getAmount();
                    if (amount > amountNeeded) {
                        invItem.setAmount(amount - amountNeeded);
                        amountNeeded = 0;
                        break;
                    } else {
                        amountNeeded -= amount;
                        player.getInventory().remove(invItem);
                    }
                }
            }

            AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class)
                    .progress(pool.getId(), holder.getId(), id, remainingAmount - amountNeeded);

            future.complete(null);
        }, () -> future.complete(null));

        return future;
    }

    public String getTaskType() {
        return config.getTask();
    }

    public boolean isCompleted(Player player) {
        if (holder.isCompleted(player)) return true;
        var data = AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class);
        var count = data.getProgression(pool.getId(), holder.getId(), id);
        return count >= config.getArgs().getInt("amount", 1);
    }

    public String getDisplay(Player player) {
        var gc = AuroraQuests.getInstance().getConfigManager().getCommonMenuConfig().getTaskStatuses();
        var data = AuroraAPI.getUser(player.getUniqueId()).getData(QuestData.class);
        var required = config.getArgs().getInt("amount", 1);
        var current = isCompleted(player) ? required : data.getProgression(pool.getId(), holder.getId(), id);

        return Placeholder.execute(config.getDisplay(),
                Placeholder.of("{status}", isCompleted(player) ? gc.getCompleted() : gc.getNotCompleted()),
                Placeholder.of("{current}", AuroraAPI.formatNumber(Math.min(current, required))),
                Placeholder.of("{required}", AuroraAPI.formatNumber(required))
        );
    }
}
