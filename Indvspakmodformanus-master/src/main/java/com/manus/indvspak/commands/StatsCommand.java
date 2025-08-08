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
import java.util.Map;

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
                .then(CommandManager.literal("teams")
                    .executes(StatsCommand::resetTeamStats))
                .then(CommandManager.literal("player")
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(StatsCommand::resetPlayerStats))))
            .executes(StatsCommand::showOwnStats));
    }

    private static int showOwnStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showPlayerStatsInternal(context.getSource(), player);
    }

    private static int showPlayerStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        return showPlayerStatsInternal(source, target);
    }

    private static int showPlayerStatsInternal(ServerCommandSource source, ServerPlayerEntity player) {
        int kills = KillTracker.getPlayerKills(player.getUuid());
        int deaths = KillTracker.getPlayerDeaths(player.getUuid());
        double kdRatio = KillTracker.getPlayerKDRatio(player.getUuid());
        TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());

        source.sendFeedback(() -> Text.literal("=== Stats for " + player.getName().getString() + " ==="), false);
        source.sendFeedback(() -> Text.literal("Team: ").append(team.getFormattedName()), false);
        source.sendFeedback(() -> Text.literal("Kills: " + kills).formatted(Formatting.GREEN), false);
        source.sendFeedback(() -> Text.literal("Deaths: " + deaths).formatted(Formatting.RED), false);
        source.sendFeedback(() -> Text.literal("K/D Ratio: " + KD_FORMAT.format(kdRatio)).formatted(Formatting.YELLOW), false);

        return 1;
    }

    private static int showTeamStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Team Statistics ==="), false);
        
        for (TeamManager.Team team : TeamManager.Team.values()) {
            int kills = KillTracker.getTeamKills(team);
            int deaths = KillTracker.getTeamDeaths(team);
            double kdRatio = KillTracker.getTeamKDRatio(team);
            
            Text teamStats = team.getFormattedName()
                .copy()
                .append(": " + kills + " kills, " + deaths + " deaths, " + KD_FORMAT.format(kdRatio) + " K/D");
            source.sendFeedback(() -> teamStats, false);
        }

        return 1;
    }

    private static int showLeaderboard(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getServer() == null) {
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Kill Leaderboard ==="), false);
        
        // Get all online players and sort by kills
        source.getServer().getPlayerManager().getPlayerList().stream()
            .sorted((p1, p2) -> Integer.compare(
                KillTracker.getPlayerKills(p2.getUuid()), 
                KillTracker.getPlayerKills(p1.getUuid())
            ))
            .limit(10)
            .forEach(player -> {
                int kills = KillTracker.getPlayerKills(player.getUuid());
                int deaths = KillTracker.getPlayerDeaths(player.getUuid());
                double kdRatio = KillTracker.getPlayerKDRatio(player.getUuid());
                TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());
                
                Text playerStats = Text.literal(player.getName().getString())
                    .append(" (")
                    .append(team.getFormattedName())
                    .append("): " + kills + " kills, " + KD_FORMAT.format(kdRatio) + " K/D");
                source.sendFeedback(() -> playerStats, false);
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

