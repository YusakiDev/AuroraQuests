package gg.auroramc.quests.api.quest;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.levels.MatcherManager;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.quests.AuroraQuests;
import gg.auroramc.quests.api.data.QuestData;
import gg.auroramc.quests.config.quest.PoolConfig;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestPool {
    @Getter
    private final PoolConfig config;
    private MatcherManager matcherManager;
    private QuestRollerScheduler questRoller;
    private final Map<String, Quest> quests = Maps.newHashMap();

    public QuestPool(PoolConfig config, RewardFactory rewardFactory) {
        this.config = config;

        if (config.getLeveling().getEnabled()) {
            matcherManager = new MatcherManager(rewardFactory);
            matcherManager.reload(config.getLeveling().getLevelMatchers(), config.getLeveling().getCustomLevels());
        }

        for (var q : config.getQuests().entrySet()) {
            quests.put(q.getKey(), new Quest(this, q.getValue(), rewardFactory));
        }

        if (isTimedRandom()) {
            questRoller = new QuestRollerScheduler(this);
        }
    }

    public String getId() {
        return config.getId();
    }

    public boolean isGlobal() {
        return config.getType().equals("global");
    }

    public boolean isTimedRandom() {
        return config.getType().equals("timed-random");
    }

    public boolean hasLeveling() {
        return config.getLeveling().getEnabled();
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    private QuestData getQuestData(Player player) {
        return AuroraAPI.getUserManager().getUser(player).getData(QuestData.class);
    }

    public List<Quest> getPlayerQuests(Player player) {
        var data = getQuestData(player);
        var rolledQuests = data.getPoolRollData(config.getId());
        return rolledQuests.quests().stream().map(this::getQuest).toList();
    }

    public int getPlayerLevel(Player player) {
        return getQuestData(player).getPoolLevel(config.getId());
    }

    public Collection<Quest> getQuests() {
        return quests.values();
    }

    public boolean rollIfNecessary(Player player, boolean sendNotification) {
        if (!isTimedRandom()) return false;
        if (!questRoller.isValid()) return false;
        var data = getQuestData(player);

        boolean hasInvalidQuests = data.getPoolRollData(getId()).quests().stream().anyMatch(q -> !quests.containsKey(q));
        var prevRollTime = questRoller.getPreviousRollDate().getTime();
        var userPrevRoll = data.getPoolRollData(getId()).timestamp();

        if (userPrevRoll == null || userPrevRoll < prevRollTime || hasInvalidQuests) {
            reRollQuests(player, sendNotification);
            return true;
        }

        return false;
    }

    public void reRollQuests(Player player, boolean sendNotification) {
        // difficulty -> quest
        var pickedQuests = new HashMap<String, List<Quest>>();
        var difficulties = config.getDifficulties();

        var questsToSelectFrom = quests.values().stream()
                .filter(q -> difficulties.containsKey(q.getDifficulty()) && q.canStart(player))
                .toList();

        var pickableQuests = new HashMap<String, List<Quest>>();

        for (var quest : questsToSelectFrom) {
            if (quest.getDifficulty() == null) continue;
            pickableQuests.computeIfAbsent(quest.getDifficulty(), k -> new ArrayList<>()).add(quest);
        }

        for (var difficulty : pickableQuests.keySet()) {
            var quests = pickableQuests.get(difficulty);
            Collections.shuffle(quests);
        }

        for (var difficulty : difficulties.entrySet()) {
            var quests = pickableQuests.get(difficulty.getKey());
            if (quests == null || quests.isEmpty()) {
                pickedQuests.put(difficulty.getKey(), Collections.emptyList());
                continue;
            }
            pickedQuests.put(difficulty.getKey(), quests.subList(0, Math.max(difficulty.getValue(), quests.size())));
        }

        var data = getQuestData(player);
        data.setRolledQuests(getId(), pickedQuests.values().stream().flatMap(List::stream).map(Quest::getId).toList());

        if(sendNotification) {
            var msg = AuroraQuests.getInstance().getConfigManager().getMessageConfig().getReRolledTarget();
            Chat.sendMessage(player, msg, Placeholder.of("{pool}", config.getName()));
        }
    }

    public void dispose() {
        if (questRoller != null) {
            questRoller.shutdown();
        }
    }
}
