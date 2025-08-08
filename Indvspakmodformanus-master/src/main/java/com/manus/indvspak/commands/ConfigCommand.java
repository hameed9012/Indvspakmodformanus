package com.manus.indvspak.commands;

import com.manus.indvspak.config.ModConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("indvspakconfig")
            .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
            .then(CommandManager.literal("reload")
                .executes(ConfigCommand::reloadConfig))
            .then(CommandManager.literal("save")
                .executes(ConfigCommand::saveConfig))
            .then(CommandManager.literal("show")
                .executes(ConfigCommand::showConfig))
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("enableIPDetection")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "enableIPDetection"))))
                .then(CommandManager.literal("enableTeamChat")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "enableTeamChat"))))
                .then(CommandManager.literal("enableKillTracking")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "enableKillTracking"))))
                .then(CommandManager.literal("enableProtection")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "enableProtection"))))
                .then(CommandManager.literal("broadcastTeamSwitches")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "broadcastTeamSwitches"))))
                .then(CommandManager.literal("broadcastKills")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "broadcastKills"))))
                .then(CommandManager.literal("allowNeutralTeam")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(context -> setBooleanConfig(context, "allowNeutralTeam"))))
                .then(CommandManager.literal("killStreakThreshold")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1, 50))
                        .executes(ConfigCommand::setKillStreakThreshold)))
                .then(CommandManager.literal("teamChatPrefix")
                    .then(CommandManager.argument("value", StringArgumentType.string())
                        .executes(ConfigCommand::setTeamChatPrefix))))
            .executes(ConfigCommand::showHelp));
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        ModConfig.getInstance().reloadConfig();
        source.sendFeedback(() -> Text.literal("Configuration reloaded successfully!").formatted(Formatting.GREEN), true);
        
        return 1;
    }

    private static int saveConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        ModConfig.getInstance().saveConfig();
        source.sendFeedback(() -> Text.literal("Configuration saved successfully!").formatted(Formatting.GREEN), true);
        
        return 1;
    }

    private static int showConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ModConfig config = ModConfig.getInstance();
        
        source.sendFeedback(() -> Text.literal("=== India vs Pakistan Mod Configuration ==="), false);
        source.sendFeedback(() -> Text.literal("enableIPDetection: " + config.enableIPDetection), false);
        source.sendFeedback(() -> Text.literal("enableTeamChat: " + config.enableTeamChat), false);
        source.sendFeedback(() -> Text.literal("enableKillTracking: " + config.enableKillTracking), false);
        source.sendFeedback(() -> Text.literal("enableProtection: " + config.enableProtection), false);
        source.sendFeedback(() -> Text.literal("broadcastTeamSwitches: " + config.broadcastTeamSwitches), false);
        source.sendFeedback(() -> Text.literal("broadcastKills: " + config.broadcastKills), false);
        source.sendFeedback(() -> Text.literal("allowNeutralTeam: " + config.allowNeutralTeam), false);
        source.sendFeedback(() -> Text.literal("persistPlayerData: " + config.persistPlayerData), false);
        source.sendFeedback(() -> Text.literal("teamChatPrefix: \"" + config.teamChatPrefix + "\""), false);
        source.sendFeedback(() -> Text.literal("killStreakThreshold: " + config.killStreakThreshold), false);
        source.sendFeedback(() -> Text.literal("trackTeamKills: " + config.trackTeamKills), false);
        source.sendFeedback(() -> Text.literal("announceFirstBlood: " + config.announceFirstBlood), false);
        source.sendFeedback(() -> Text.literal("announceKillStreaks: " + config.announceKillStreaks), false);
        
        return 1;
    }

    private static int setBooleanConfig(CommandContext<ServerCommandSource> context, String configKey) {
        ServerCommandSource source = context.getSource();
        boolean value = BoolArgumentType.getBool(context, "value");
        ModConfig config = ModConfig.getInstance();
        
        try {
            switch (configKey) {
                case "enableIPDetection":
                    config.enableIPDetection = value;
                    break;
                case "enableTeamChat":
                    config.enableTeamChat = value;
                    break;
                case "enableKillTracking":
                    config.enableKillTracking = value;
                    break;
                case "enableProtection":
                    config.enableProtection = value;
                    break;
                case "broadcastTeamSwitches":
                    config.broadcastTeamSwitches = value;
                    break;
                case "broadcastKills":
                    config.broadcastKills = value;
                    break;
                case "allowNeutralTeam":
                    config.allowNeutralTeam = value;
                    break;
                default:
                    source.sendError(Text.literal("Unknown configuration key: " + configKey));
                    return 0;
            }
            
            config.saveConfig();
            source.sendFeedback(() -> Text.literal("Set " + configKey + " to " + value).formatted(Formatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to set configuration: " + e.getMessage()));
            return 0;
        }
    }

    private static int setKillStreakThreshold(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int value = IntegerArgumentType.getInteger(context, "value");
        ModConfig config = ModConfig.getInstance();
        
        config.killStreakThreshold = value;
        config.saveConfig();
        
        source.sendFeedback(() -> Text.literal("Set killStreakThreshold to " + value).formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int setTeamChatPrefix(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String value = StringArgumentType.getString(context, "value");
        ModConfig config = ModConfig.getInstance();
        
        config.teamChatPrefix = value;
        config.saveConfig();
        
        source.sendFeedback(() -> Text.literal("Set teamChatPrefix to \"" + value + "\"").formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("India vs Pakistan Mod Configuration Commands:"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig reload - Reload configuration from file"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig save - Save current configuration to file"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig show - Show current configuration"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig set <setting> <value> - Change a configuration setting"), false);
        source.sendFeedback(() -> Text.literal("Available settings: enableIPDetection, enableTeamChat, enableKillTracking,"), false);
        source.sendFeedback(() -> Text.literal("enableProtection, broadcastTeamSwitches, broadcastKills, allowNeutralTeam,"), false);
        source.sendFeedback(() -> Text.literal("killStreakThreshold, teamChatPrefix"), false);
        
        return 1;
    }
}

