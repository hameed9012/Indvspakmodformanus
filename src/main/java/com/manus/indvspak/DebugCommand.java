package com.manus.indvspak.commands;

import com.manus.indvspak.TeamManager;
import com.manus.indvspak.config.ModConfig;
import com.manus.indvspak.protection.RegionManager;
import com.manus.indvspak.stats.KillTracker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.UUID;

public class DebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("indvspakdebug")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                .then(CommandManager.literal("status")
                        .executes(DebugCommand::showStatus))
                .then(CommandManager.literal("reload")
                        .executes(DebugCommand::reloadAll))
                .executes(DebugCommand::showHelp));
    }

    private static int showStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("=== India vs Pakistan Mod Debug Status ===").formatted(Formatting.GOLD), false);

        // Config status
        ModConfig config = ModConfig.getInstance();
        source.sendFeedback(() -> Text.literal("Config loaded: " + (config != null ? "YES" : "NO")).formatted(Formatting.GREEN), false);
        if (config != null) {
            source.sendFeedback(() -> Text.literal("IP Detection: " + config.enableIPDetection), false);
            source.sendFeedback(() -> Text.literal("Kill Tracking: " + config.enableKillTracking), false);
            source.sendFeedback(() -> Text.literal("Protection: " + config.enableProtection), false);
        }

        // Regions status
        Map<String, RegionManager.ProtectedRegion> regions = RegionManager.getAllRegions();
        source.sendFeedback(() -> Text.literal("Regions loaded: " + regions.size()), false);

        // Players status
        if (source.getServer() != null) {
            int playerCount = source.getServer().getPlayerManager().getCurrentPlayerCount();
            source.sendFeedback(() -> Text.literal("Online players: " + playerCount), false);

            // Team distribution
            int indiaCount = 0, pakistanCount = 0, neutralCount = 0;
            source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());
                switch (team) {
                    case INDIA -> indiaCount++;
                    case PAKISTAN -> pakistanCount++;
                    case NEUTRAL -> neutralCount++;
                }
            });

            final int finalIndiaCount = indiaCount;
            final int finalPakistanCount = pakistanCount;
            final int finalNeutralCount = neutralCount;

            source.sendFeedback(() -> Text.literal("Team India: " + finalIndiaCount + " players"), false);
            source.sendFeedback(() -> Text.literal("Team Pakistan: " + finalPakistanCount + " players"), false);
            source.sendFeedback(() -> Text.literal("Team Neutral: " + finalNeutralCount + " players"), false);
        }

        // Kill stats
        Map<UUID, Integer> playerKills = KillTracker.getAllPlayerKills();
        source.sendFeedback(() -> Text.literal("Players with kill data: " + playerKills.size()), false);

        return 1;
    }

    private static int reloadAll(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            // Reload config
            ModConfig.getInstance().reloadConfig();

            // Reload regions
            RegionManager.loadRegions();

            source.sendFeedback(() -> Text.literal("All mod data reloaded successfully!").formatted(Formatting.GREEN), true);
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to reload mod data: " + e.getMessage()));
            return 0;
        }

        return 1;
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("Debug Commands:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/indvspakdebug status - Show mod status"), false);
        source.sendFeedback(() -> Text.literal("/indvspakdebug reload - Reload all mod data"), false);

        return 1;
    }
}