package com.manus.indvspak.commands;

import com.manus.indvspak.TeamManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class TeamCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("team")
            .then(CommandManager.literal("switch")
                .then(CommandManager.argument("team", StringArgumentType.string())
                    .suggests(TEAM_SUGGESTIONS)
                    .executes(TeamCommand::switchTeam)))
            .then(CommandManager.literal("info")
                .executes(TeamCommand::showTeamInfo))
            .then(CommandManager.literal("list")
                .executes(TeamCommand::listTeams))
            .executes(TeamCommand::showHelp));
    }

    private static final SuggestionProvider<ServerCommandSource> TEAM_SUGGESTIONS = (context, builder) -> {
        return CommandSource.suggestMatching(new String[]{"india", "pakistan", "neutral"}, builder);
    };

    private static int switchTeam(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
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
                player.sendMessage(Text.literal("Invalid team name. Available teams: india, pakistan, neutral"), false);
                return 0;
        }

        boolean success = TeamManager.switchPlayerTeam(player, newTeam);
        return success ? 1 : 0;
    }

    private static int showTeamInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        TeamManager.Team currentTeam = TeamManager.getPlayerTeam(player.getUuid());

        Text message = Text.literal("Your current team: ").append(currentTeam.getFormattedName());
        player.sendMessage(message, false);

        return 1;
    }

    private static int listTeams(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("Available teams:"), false);
        source.sendFeedback(() -> Text.literal("- ").append(TeamManager.Team.INDIA.getFormattedName()), false);
        source.sendFeedback(() -> Text.literal("- ").append(TeamManager.Team.PAKISTAN.getFormattedName()), false);
        source.sendFeedback(() -> Text.literal("- ").append(TeamManager.Team.NEUTRAL.getFormattedName()), false);

        return 1;
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("Team Commands:"), false);
        source.sendFeedback(() -> Text.literal("/team switch <team> - Switch to a different team"), false);
        source.sendFeedback(() -> Text.literal("/team info - Show your current team"), false);
        source.sendFeedback(() -> Text.literal("/team list - List all available teams"), false);

        return 1;
    }
}

