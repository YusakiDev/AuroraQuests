package gg.auroramc.quests.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class MessageConfig extends AuroraConfig {

    private String reloaded = "&aReloaded configuration!";
    private String dataNotLoadedYet = "&cData for this player hasn't loaded yet, try again later!";
    private String dataNotLoadedYetSelf = "&cYour data isn't loaded yet, please try again later!";
    private String playerOnlyCommand = "&cThis command can only be executed by a player!";
    private String noPermission = "&cYou don't have permission to execute this command!";
    private String invalidSyntax = "&cInvalid command syntax!";
    private String mustBeNumber = "&cArgument must be a number!";
    private String playerNotFound = "&cPlayer not found!";
    private String commandError = "&cAn error occurred while executing this command!";
    private String menuOpened = "&aOpened collection menu for {player}";
    private String reRolledTarget = "&aYour quests for {pool} have been re-rolled!";
    private String reRolledSource = "&aQuests for {player} for pool {pool} have been re-rolled!";
    private String globalQuestUnlocked = "&aYou have unlocked the {quest} quest in {pool}!";
    private String poolNotFound = "&cThere isn't any quest line with this id: {pool}!";
    private String poolUnlocked = "&aYou have unlocked a new quest pool: {pool}!";
    private String unknownCommand = "&cUnknown Command, please type /help";
    private String questNotFound = "&cThere isn't any quest with id {quest} in pool {pool}!";
    private String questUnlocked = "&aQuest {quest} unlocked for {player}.";
    private String questAlreadyUnlocked = "&cPlayer {player} has already unlocked quest {quest}.";
    private String errorPrefix = "&cError: {message}";

    public MessageConfig(AuroraQuests plugin, String language) {
        super(getFile(plugin, language));
    }

    private static File getFile(AuroraQuests plugin, String language) {
        return new File(plugin.getDataFolder(), "messages_" + language + ".yml");
    }

    public static void saveDefault(AuroraQuests plugin, String language) {
        if (!getFile(plugin, language).exists()) {
            try {
                plugin.saveResource("messages_" + language + ".yml", false);
            } catch (Exception e) {
                AuroraQuests.logger().warning("Internal message file for language: " + language + " not found! Creating a new one from english...");

                var file = getFile(plugin, language);


                try (InputStream in = plugin.getResource("messages_en.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException ex) {
                    AuroraQuests.logger().severe("Failed to create message file for language: " + language);
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected List<Consumer<YamlConfiguration>> getMigrationSteps() {
        return List.of(
                (yaml) -> {
                    yaml.set("quest-not-found", "&cThere isn't any quest with id {quest} in pool {pool}!");
                    yaml.set("quest-unlocked", "&aQuest {quest} unlocked for {player}.");
                    yaml.set("quest-already-unlocked", "&cPlayer {player} has already unlocked quest {quest}.");
                    yaml.set("error-prefix", "&cError: {message}");
                    yaml.set("config-version", 1);
                }
        );
    }
}
