package com.manus.indvspak.commands;

import com.manus.indvspak.TeamManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class AdminCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("teamadmin")
            .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
            .then(CommandManager.literal("assign")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("team", StringArgumentType.string())
                        .suggests(TEAM_SUGGESTIONS)
                        .executes(AdminCommand::assignPlayerToTeam))))
            .then(CommandManager.literal("list")
                .executes(AdminCommand::listAllPlayers))
            .then(CommandManager.literal("stats")
                .executes(AdminCommand::showTeamStats))
            .executes(AdminCommand::showAdminHelp));
    }

    private static final SuggestionProvider<ServerCommandSource> TEAM_SUGGESTIONS = (context, builder) -> {
        return CommandSource.suggestMatching(new String[]{"india", "pakistan", "neutral"}, builder);
    };

    private static int assignPlayerToTeam(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
        String teamName = StringArgumentType.getString(context, "team").toLowerCase();

        TeamManager.Team newTeam;
        switch (teamName) {
            case "india":
                newTeam = TeamManager.Team.INDIA;
                break;
            case "pakistan":
                newTeam = TeamManager.Team.PAKISTAN;
                break;
            case "neutral":
                newTeam = TeamManager.Team.NEUTRAL;
                break;
            default:
                source.sendError(Text.literal("Invalid team name. Available teams: india, pakistan, neutral"));
                return 0;
        }

        TeamManager.setPlayerTeam(targetPlayer.getUuid(), newTeam);
        
        Text message = Text.literal("Assigned " + targetPlayer.getName().getString() + " to team ")
                .append(newTeam.getFormattedName());
        source.sendFeedback(() -> message, true);

        // Notify the target player
        Text playerMessage = Text.literal("An admin has assigned you to team ").append(newTeam.getFormattedName());
        targetPlayer.sendMessage(playerMessage, false);

        return 1;
    }

    private static int listAllPlayers(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getServer() == null) {
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Team Assignments ==="), false);
        
        int indiaCount = 0, pakistanCount = 0, neutralCount = 0;

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());
            Text playerInfo = Text.literal(player.getName().getString() + " - ").append(team.getFormattedName());
            source.sendFeedback(() -> playerInfo, false);

            switch (team) {
                case INDIA:
                    indiaCount++;
                    break;
                case PAKISTAN:
                    pakistanCount++;
                    break;
                case NEUTRAL:
                    neutralCount++;
                    break;
            }
        }
        
        source.sendFeedback(() -> Text.literal("=== Team Statistics ==="), false);
        
        final int finalIndiaCount = indiaCount;
        final int finalPakistanCount = pakistanCount;
        final int finalNeutralCount = neutralCount;
        
        source.sendFeedback(() -> Text.literal("India: " + finalIndiaCount), false);
        source.sendFeedback(() -> Text.literal("Pakistan: " + finalPakistanCount), false);
        source.sendFeedback(() -> Text.literal("Neutral: " + finalNeutralCount), false);

        return 1;
    }

    private static int showTeamStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getServer() == null) {
            return 0;
        }

        int totalPlayers = source.getServer().getPlayerManager().getCurrentPlayerCount();
        int indiaCount = 0, pakistanCount = 0, neutralCount = 0;

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            TeamManager.Team team = TeamManager.getPlayerTeam(player.getUuid());
            switch (team) {
                case INDIA:
                    indiaCount++;
                    break;
                case PAKISTAN:
                    pakistanCount++;
                    break;
                case NEUTRAL:
                    neutralCount++;
                    break;
            }
        }

        source.sendFeedback(() -> Text.literal("=== Team Distribution ==="), false);
        
        final int finalTotalPlayers = totalPlayers;
        final int finalIndiaCount2 = indiaCount;
        final int finalPakistanCount2 = pakistanCount;
        final int finalNeutralCount2 = neutralCount;
        
        source.sendFeedback(() -> Text.literal("India Team: " + finalIndiaCount2 + " (" + (finalTotalPlayers > 0 ? (finalIndiaCount2 * 100 / finalTotalPlayers) : 0) + "%)"), false);
        source.sendFeedback(() -> Text.literal("Pakistan Team: " + finalPakistanCount2 + " (" + (finalTotalPlayers > 0 ? (finalPakistanCount2 * 100 / finalTotalPlayers) : 0) + "%)"), false);
        source.sendFeedback(() -> Text.literal("Neutral Team: " + finalNeutralCount2 + " (" + (finalTotalPlayers > 0 ? (finalNeutralCount2 * 100 / finalTotalPlayers) : 0) + "%)"), false);

        return 1;
    }

    private static int showAdminHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("Team Admin Commands:"), false);
        source.sendFeedback(() -> Text.literal("/teamadmin assign <player> <team> - Assign a player to a team"), false);
        source.sendFeedback(() -> Text.literal("/teamadmin list - List all players and their teams"), false);
        source.sendFeedback(() -> Text.literal("/teamadmin stats - Show team statistics"), false);

        return 1;
    }
}

