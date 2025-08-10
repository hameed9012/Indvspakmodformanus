package com.manus.indvspak.commands;

import com.manus.indvspak.TeamManager;
import com.manus.indvspak.protection.RegionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RegionCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("region")
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .then(CommandManager.argument("x1", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("y1", IntegerArgumentType.integer())
                                                .then(CommandManager.argument("z1", IntegerArgumentType.integer())
                                                        .then(CommandManager.argument("x2", IntegerArgumentType.integer())
                                                                .then(CommandManager.argument("y2", IntegerArgumentType.integer())
                                                                        .then(CommandManager.argument("z2", IntegerArgumentType.integer())
                                                                                .then(CommandManager.argument("team", StringArgumentType.string())
                                                                                        .suggests(TEAM_SUGGESTIONS)
                                                                                        .executes(RegionCommand::createRegion))))))))))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(REGION_SUGGESTIONS)
                                .executes(RegionCommand::deleteRegion)))
                .then(CommandManager.literal("list")
                        .executes(RegionCommand::listRegions))
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(REGION_SUGGESTIONS)
                                .executes(RegionCommand::showRegionInfo)))
                .then(CommandManager.literal("here")
                        .executes(RegionCommand::showRegionsHere))
                .executes(RegionCommand::showHelp));
    }

    private static final SuggestionProvider<ServerCommandSource> TEAM_SUGGESTIONS = (context, builder) -> {
        // --- FIX: Calling the new static getNames() method from Team enum ---
        return CommandSource.suggestMatching(TeamManager.Team.getNames(), builder);
    };

    private static final SuggestionProvider<ServerCommandSource> REGION_SUGGESTIONS = (context, builder) -> {
        return CommandSource.suggestMatching(RegionManager.getAllRegions().keySet(), builder);
    };

    private static int createRegion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String name = StringArgumentType.getString(context, "name");
        String teamName = StringArgumentType.getString(context, "team");

        // --- FIX: Calling the new static fromName() method from Team enum ---
        TeamManager.Team team = TeamManager.Team.fromName(teamName);
        if (team == null) {
            player.sendMessage(Text.literal("Invalid team name: " + teamName), false);
            return 0;
        }

        int x1 = IntegerArgumentType.getInteger(context, "x1");
        int y1 = IntegerArgumentType.getInteger(context, "y1");
        int z1 = IntegerArgumentType.getInteger(context, "z1");
        int x2 = IntegerArgumentType.getInteger(context, "x2");
        int y2 = IntegerArgumentType.getInteger(context, "y2");
        int z2 = IntegerArgumentType.getInteger(context, "z2");

        BlockPos corner1 = new BlockPos(x1, y1, z1);
        BlockPos corner2 = new BlockPos(x2, y2, z2);
        String worldId = player.getWorld().getRegistryKey().getValue().toString();

        if (RegionManager.createRegion(name, team, corner1, corner2, worldId)) {
            player.sendMessage(Text.literal("Created protected region '" + name + "' for team ").append(team.getFormattedName()), false);
        } else {
            player.sendMessage(Text.literal("Failed to create region. A region with that name might already exist."), false);
        }

        return 1;
    }

    private static int deleteRegion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        if (RegionManager.deleteRegion(name)) {
            source.sendFeedback(() -> Text.literal("Deleted protected region '" + name + "'.").formatted(Formatting.GREEN), true);
        } else {
            source.sendFeedback(() -> Text.literal("Failed to delete region. Region '" + name + "' not found.").formatted(Formatting.RED), true);
        }

        return 1;
    }

    private static int listRegions(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Map<String, RegionManager.ProtectedRegion> regions = RegionManager.getAllRegions();

        source.sendFeedback(() -> Text.literal("Protected Regions:").formatted(Formatting.GOLD), false);
        if (regions.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No regions defined."), false);
        } else {
            regions.values().forEach(region -> {
                Text regionInfo = Text.literal("- " + region.name)
                        .formatted(Formatting.YELLOW)
                        .append(" (Owner: ")
                        .append(region.ownerTeam.getFormattedName())
                        .append(")");
                source.sendFeedback(() -> regionInfo, false);
            });
        }
        return 1;
    }

    private static int showRegionInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        RegionManager.ProtectedRegion region = RegionManager.getRegion(name);
        if (region == null) {
            source.sendFeedback(() -> Text.literal("Region '" + name + "' not found."), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("--- Region Info: " + region.name + " ---").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Owner: ").append(region.ownerTeam.getFormattedName()), false);
        source.sendFeedback(() -> Text.literal("World: " + region.worldId), false);
        source.sendFeedback(() -> Text.literal("Coordinates: (" + region.corner1.getX() + ", " + region.corner1.getY() + ", " + region.corner1.getZ() + ") to (" + region.corner2.getX() + ", " + region.corner2.getY() + ", " + region.corner2.getZ() + ")"), false);
        source.sendFeedback(() -> Text.literal("Allow Teammate Access: " + region.allowTeammates), false);
        source.sendFeedback(() -> Text.literal("Allow Neutral Access: " + region.allowNeutral), false);

        return 1;
    }

    private static int showRegionsHere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        BlockPos pos = player.getBlockPos();
        String worldId = player.getWorld().getRegistryKey().getValue().toString();
        List<RegionManager.ProtectedRegion> regions = RegionManager.getRegionsAt(pos, worldId);

        if (regions.isEmpty()) {
            player.sendMessage(Text.literal("No protected regions at your current location."), false);
            return 1;
        }

        player.sendMessage(Text.literal("Protected regions at your location:"), false);
        for (RegionManager.ProtectedRegion region : regions) {
            Text regionInfo = Text.literal("- " + region.name)
                    .formatted(Formatting.YELLOW)
                    .append(" (")
                    .append(region.ownerTeam.getFormattedName())
                    .append(")");
            player.sendMessage(regionInfo, false);
        }

        return 1;
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("Region Commands:"), false);
        source.sendFeedback(() -> Text.literal("/region create <name> <x1> <y1> <z1> <x2> <y2> <z2> <team> - Create a protected region"), false);
        source.sendFeedback(() -> Text.literal("/region delete <name> - Delete a protected region"), false);
        source.sendFeedback(() -> Text.literal("/region list - List all protected regions"), false);
        source.sendFeedback(() -> Text.literal("/region info <name> - Show detailed info about a region"), false);
        source.sendFeedback(() -> Text.literal("/region here - Show regions at your current location"), false);

        return 1;
    }
}
