package com.manus.indvspak.stats;

import com.manus.indvspak.TeamManager;
import com.manus.indvspak.data.PlayerDataManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillTracker {

    // Track team kill counts
    private static final Map<TeamManager.Team, Integer> teamKills = new HashMap<>();
    private static final Map<TeamManager.Team, Integer> teamDeaths = new HashMap<>();

    static {
        // Initialize team counters
        for (TeamManager.Team team : TeamManager.Team.values()) {
            teamKills.put(team, 0);
            teamDeaths.put(team, 0);
        }
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity deadPlayer) {
                handlePlayerDeath(deadPlayer, damageSource);
            }
        });
    }

    private static void handlePlayerDeath(ServerPlayerEntity deadPlayer, DamageSource damageSource) {
        // Increment player and team deaths
        PlayerDataManager.addDeath(deadPlayer.getUuid());
        TeamManager.Team deadPlayerTeam = TeamManager.getPlayerTeam(deadPlayer.getUuid());
        teamDeaths.merge(deadPlayerTeam, 1, Integer::sum);

        // Check for killer
        if (damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
            // Increment killer's stats
            PlayerDataManager.addKill(killer.getUuid());
            TeamManager.Team killerTeam = TeamManager.getPlayerTeam(killer.getUuid());

            // Check if it's a team kill
            if (deadPlayerTeam == killerTeam) {
                // Team kill logic
                Text message = Text.literal(killer.getName().getString() + " killed their teammate " + deadPlayer.getName().getString() + "!")
                        .formatted(Formatting.RED);
                if (killer.getServer() != null) {
                    killer.getServer().getPlayerManager().broadcast(message, false);
                }
            } else {
                // Regular kill logic
                Text message = Text.literal(killer.getName().getString())
                        .append(Text.literal(" [" + killerTeam.getDisplayName() + "] ").formatted(killerTeam.getColor()))
                        .append(" killed ")
                        .append(deadPlayer.getName().getString())
                        .append(Text.literal(" [" + deadPlayerTeam.getDisplayName() + "] ").formatted(deadPlayerTeam.getColor()));

                if (killer.getServer() != null) {
                    killer.getServer().getPlayerManager().broadcast(message, false);
                }

                // Increment killer's team kills
                teamKills.merge(killerTeam, 1, Integer::sum);
            }
        }
    }

    public static int getPlayerKills(UUID playerId) {
        return PlayerDataManager.getKills(playerId);
    }

    public static int getPlayerDeaths(UUID playerId) {
        return PlayerDataManager.getDeaths(playerId);
    }

    public static double getPlayerKDRatio(UUID playerId) {
        int kills = getPlayerKills(playerId);
        int deaths = getPlayerDeaths(playerId);
        return deaths > 0 ? (double) kills / deaths : kills;
    }

    public static int getTeamKills(TeamManager.Team team) {
        return teamKills.getOrDefault(team, 0);
    }

    public static int getTeamDeaths(TeamManager.Team team) {
        return teamDeaths.getOrDefault(team, 0);
    }

    public static double getTeamKDRatio(TeamManager.Team team) {
        int kills = getTeamKills(team);
        int deaths = getTeamDeaths(team);
        return deaths > 0 ? (double) kills / deaths : kills;
    }

    public static Map<TeamManager.Team, Integer> getAllTeamKills() {
        return new HashMap<>(teamKills);
    }

    public static Map<TeamManager.Team, Integer> getAllTeamDeaths() {
        return new HashMap<>(teamDeaths);
    }

    // New method to get all player kills for the leaderboard
    public static Map<UUID, Integer> getAllPlayerKills() {
        Map<UUID, Integer> playerKills = new HashMap<>();
        PlayerDataManager.getAllPlayerData().forEach((uuid, playerData) -> {
            playerKills.put(uuid, playerData.kills);
        });
        return playerKills;
    }

    public static void resetTeamStats() {
        for (TeamManager.Team team : TeamManager.Team.values()) {
            teamKills.put(team, 0);
            teamDeaths.put(team, 0);
        }
    }

    public static void resetPlayerStats(UUID playerId) {
        // Direct access to the public fields of PlayerData to reset kills and deaths
        PlayerDataManager.getPlayerData(playerId).kills = 0;
        PlayerDataManager.getPlayerData(playerId).deaths = 0;
    }
}
