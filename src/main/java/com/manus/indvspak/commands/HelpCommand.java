package com.manus.indvspak.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HelpCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("indvspakhelp")
            .then(CommandManager.literal("team")
                .executes(HelpCommand::showTeamHelp))
            .then(CommandManager.literal("region")
                .executes(HelpCommand::showRegionHelp))
            .then(CommandManager.literal("stats")
                .executes(HelpCommand::showStatsHelp))
            .then(CommandManager.literal("chat")
                .executes(HelpCommand::showChatHelp))
            .then(CommandManager.literal("admin")
                .executes(HelpCommand::showAdminHelp))
            .executes(HelpCommand::showGeneralHelp));
    }

    private static int showGeneralHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== India vs Pakistan Mod Help ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("This mod creates a team-based PvP experience with India and Pakistan teams."), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Key Features:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("• Automatic team assignment based on IP location"), false);
        source.sendFeedback(() -> Text.literal("• Protected home bases for each team"), false);
        source.sendFeedback(() -> Text.literal("• Kill/death tracking and statistics"), false);
        source.sendFeedback(() -> Text.literal("• Team chat and global chat with prefixes"), false);
        source.sendFeedback(() -> Text.literal("• Anti-griefing protection"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Help Categories:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/indvspakhelp team - Team management commands"), false);
        source.sendFeedback(() -> Text.literal("/indvspakhelp region - Region protection commands"), false);
        source.sendFeedback(() -> Text.literal("/indvspakhelp stats - Statistics and leaderboard commands"), false);
        source.sendFeedback(() -> Text.literal("/indvspakhelp chat - Chat and communication commands"), false);
        source.sendFeedback(() -> Text.literal("/indvspakhelp admin - Admin and configuration commands"), false);
        
        return 1;
    }

    private static int showTeamHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Team Commands ===").formatted(Formatting.GREEN), false);
        source.sendFeedback(() -> Text.literal("/team - Show team command help"), false);
        source.sendFeedback(() -> Text.literal("/team info - Show your current team"), false);
        source.sendFeedback(() -> Text.literal("/team list - List all available teams"), false);
        source.sendFeedback(() -> Text.literal("/team switch <team> - Switch to a different team"), false);
        source.sendFeedback(() -> Text.literal("  Available teams: india, pakistan, neutral"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Team Assignment:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("• Players are automatically assigned based on IP location"), false);
        source.sendFeedback(() -> Text.literal("• Indian IPs → India team, Pakistani IPs → Pakistan team"), false);
        source.sendFeedback(() -> Text.literal("• Other locations → Neutral team"), false);
        source.sendFeedback(() -> Text.literal("• You can switch teams manually using /team switch"), false);
        
        return 1;
    }

    private static int showRegionHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Region Protection Commands ===").formatted(Formatting.BLUE), false);
        source.sendFeedback(() -> Text.literal("/region - Show region command help"), false);
        source.sendFeedback(() -> Text.literal("/region here - Show regions at your current location"), false);
        source.sendFeedback(() -> Text.literal("/region list - List all protected regions"), false);
        source.sendFeedback(() -> Text.literal("/region info <name> - Show detailed info about a region"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Admin Commands:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/region create <name> <x1> <y1> <z1> <x2> <y2> <z2> <team>"), false);
        source.sendFeedback(() -> Text.literal("  - Create a new protected region"), false);
        source.sendFeedback(() -> Text.literal("/region delete <name> - Delete a protected region"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Protection Features:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("• Prevents opposing teams from breaking/placing blocks"), false);
        source.sendFeedback(() -> Text.literal("• Protects against griefing and theft"), false);
        source.sendFeedback(() -> Text.literal("• Team members can access their team's regions"), false);
        
        return 1;
    }

    private static int showStatsHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Statistics Commands ===").formatted(Formatting.RED), false);
        source.sendFeedback(() -> Text.literal("/stats - Show your own statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats player <player> - Show another player's statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats team - Show team statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats leaderboard - Show kill leaderboard"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Admin Commands:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/stats reset teams - Reset all team statistics"), false);
        source.sendFeedback(() -> Text.literal("/stats reset player <player> - Reset a player's statistics"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Tracked Statistics:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("• Individual kills and deaths"), false);
        source.sendFeedback(() -> Text.literal("• Kill/Death ratio (K/D)"), false);
        source.sendFeedback(() -> Text.literal("• Team kill counts"), false);
        source.sendFeedback(() -> Text.literal("• Leaderboards and rankings"), false);
        
        return 1;
    }

    private static int showChatHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Chat Commands ===").formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("/tc - Toggle team chat mode"), false);
        source.sendFeedback(() -> Text.literal("/tc <message> - Send a team chat message"), false);
        source.sendFeedback(() -> Text.literal("/teamchat - Same as /tc"), false);
        source.sendFeedback(() -> Text.literal("/gc <message> - Send a global message (when in team chat mode)"), false);
        source.sendFeedback(() -> Text.literal("/globalchat <message> - Same as /gc"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Chat Features:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("• All messages show team prefixes: [India], [Pakistan], [Neutral]"), false);
        source.sendFeedback(() -> Text.literal("• Team chat: Only visible to your team members"), false);
        source.sendFeedback(() -> Text.literal("• Global chat: Visible to all players"), false);
        source.sendFeedback(() -> Text.literal("• Toggle between team and global chat modes"), false);
        source.sendFeedback(() -> Text.literal("• Kill announcements with team colors"), false);
        
        return 1;
    }

    private static int showAdminHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("=== Admin Commands ===").formatted(Formatting.DARK_RED), false);
        source.sendFeedback(() -> Text.literal("Team Management:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/teamadmin assign <player> <team> - Assign player to team"), false);
        source.sendFeedback(() -> Text.literal("/teamadmin list - List all players and their teams"), false);
        source.sendFeedback(() -> Text.literal("/teamadmin stats - Show team statistics"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Configuration:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig show - Show current configuration"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig reload - Reload configuration from file"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig save - Save configuration to file"), false);
        source.sendFeedback(() -> Text.literal("/indvspakconfig set <setting> <value> - Change a setting"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Region Management:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("See /indvspakhelp region for region commands"), false);
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("Statistics Management:").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> Text.literal("See /indvspakhelp stats for statistics commands"), false);
        
        return 1;
    }
}

