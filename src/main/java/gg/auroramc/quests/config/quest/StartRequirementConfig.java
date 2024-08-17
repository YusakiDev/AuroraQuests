package gg.auroramc.quests.config.quest;

import lombok.Getter;

import java.util.List;

@Getter
public class StartRequirementConfig {
    private boolean alwaysShowInMenu = false;
    private boolean needCommandToStart = false;
    private List<String> quests;
    private List<String> permissions;
}
