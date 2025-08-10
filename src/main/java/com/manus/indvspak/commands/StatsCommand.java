package com.manus.indvspak.commands;

import com.manus.indvspak.TeamManager;
import com.manus.indvspak.stats.KillTracker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsCommand {

    private static final DecimalFormat KD_FORMAT = new DecimalFormat("#.##");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stats")
                .then(CommandManager.literal("player")
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(StatsCommand::showPlayerStats))
                        .executes(StatsCommand::showOwnStats))
                .then(CommandManager.literal("team")
                        .executes(StatsCommand::showTeamStats))
                .then(CommandManager.literal("leaderboard")
                        .executes(StatsCommand::showLeaderboard))
                .then(CommandManager.literal("reset")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("team")
                                .executes(StatsCommand::resetTeamStats))
                        .then(CommandManager.literal("player")
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(StatsCommand::resetPlayerStats))))
                .executes(StatsCommand::showHelp));
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("Stats Commands:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/stats player [player] - Show a player's statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats team - Show team statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats leaderboard - Show the top players by kills"), false);
        source.sendFeedback(() -> Text.literal("/stats reset team - Reset all team statistics (OP only)"), false);
        source.sendFeedback(() -> Text.literal("/stats reset player <player> - Reset a specific player's statistics (OP only)"), false);

        return 1;
    }

    private static int showOwnStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showPlayerStatsFor(context, player);
    }

    private static int showPlayerStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        return showPlayerStatsFor(context, target);
    }

    private static int showPlayerStatsFor(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) {
        ServerCommandSource source = context.getSource();

        UUID playerId = target.getUuid();
        int kills = KillTracker.getPlayerKills(playerId);
        int deaths = KillTracker.getPlayerDeaths(playerId);
        double kdRatio = KillTracker.getPlayerKDRatio(playerId);
        TeamManager.Team team = TeamManager.getPlayerTeam(playerId);

        Text statsMessage = Text.literal(target.getName().getString())
                .append(Text.literal("'s Stats: ").formatted(Formatting.YELLOW))
                .append(Text.literal("\nTeam: ").formatted(Formatting.WHITE)).append(team.getFormattedName())
                .append(Text.literal("\nKills: ").formatted(Formatting.WHITE)).append(Text.literal(String.valueOf(kills)).formatted(Formatting.GREEN))
                .append(Text.literal("\nDeaths: ").formatted(Formatting.WHITE)).append(Text.literal(String.valueOf(deaths)).formatted(Formatting.RED))
                .append(Text.literal("\nK/D: ").formatted(Formatting.WHITE)).append(Text.literal(KD_FORMAT.format(kdRatio)).formatted(Formatting.AQUA));

        source.sendFeedback(() -> statsMessage, false);

        return 1;
    }

    private static int showTeamStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("Team Statistics:").formatted(Formatting.GOLD), false);

        for (TeamManager.Team team : TeamManager.Team.values()) {
            if (team == TeamManager.Team.NEUTRAL) continue; // Don't show neutral team stats

            int kills = KillTracker.getTeamKills(team);
            int deaths = KillTracker.getTeamDeaths(team);
            double kdRatio = KillTracker.getTeamKDRatio(team);

            Text teamStats = Text.literal("- ").append(team.getFormattedName())
                    .append(Text.literal(": " + kills + " kills, " + deaths + " deaths, " + KD_FORMAT.format(kdRatio) + " K/D"));

            source.sendFeedback(() -> teamStats, false);
        }

        return 1;
    }

    private static int showLeaderboard(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Get all player kill data from the KillTracker
        Map<UUID, Integer> allPlayerKills = KillTracker.getAllPlayerKills();

        // Create a sorted list of players by kills (descending)
        allPlayerKills.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10) // Show top 10
                .forEach(entry -> {
                    // Find the player's name from their UUID
                    ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(entry.getKey());
                    if (player != null) {
                        TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());
                        Text playerStats = Text.literal(player.getName().getString())
                                .append(" (").append(team.getFormattedName()).append(")")
                                .append(": " + entry.getValue() + " kills");
                        source.sendFeedback(() -> playerStats, false);
                    }
                });

        return 1;
    }

    private static int resetTeamStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        KillTracker.resetTeamStats();
        source.sendFeedback(() -> Text.literal("Team statistics have been reset.").formatted(Formatting.GREEN), true);

        return 1;
    }

    private static int resetPlayerStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");

        KillTracker.resetPlayerStats(target.getUuid());

        Text message = Text.literal("Statistics for " + target.getName().getString() + " have been reset.")
                .formatted(Formatting.GREEN);
        source.sendFeedback(() -> message, true);

        target.sendMessage(Text.literal("Your statistics have been reset by an admin.").formatted(Formatting.YELLOW), false);

        return 1;
    }
}
