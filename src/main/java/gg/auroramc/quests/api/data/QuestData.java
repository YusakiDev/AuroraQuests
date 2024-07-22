package gg.auroramc.quests.api.data;

import gg.auroramc.aurora.api.user.UserDataHolder;
import gg.auroramc.aurora.api.util.NamespacedId;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class QuestData extends UserDataHolder {
    @Override
    public NamespacedId getId() {
        return NamespacedId.fromDefault("quests");
    }

    @Override
    public void serializeInto(ConfigurationSection configurationSection) {

    }

    @Override
    public void initFrom(@Nullable ConfigurationSection configurationSection) {

    }
}
