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
        TeamManager.Team deadPlayerTeam = TeamManager.getPlayerTeam(deadPlayer.getUuid());
        
        // Record death
        PlayerDataManager.addDeath(deadPlayer.getUuid());
        incrementTeamDeaths(deadPlayerTeam);
        
        // Check if killed by another player
        if (damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
            TeamManager.Team killerTeam = TeamManager.getPlayerTeam(killer.getUuid());
            
            // Record kill
            PlayerDataManager.addKill(killer.getUuid());
            incrementTeamKills(killerTeam);
            
            // Broadcast kill message
            broadcastKillMessage(killer, killerTeam, deadPlayer, deadPlayerTeam);
            
            // Send personal messages
            sendKillMessages(killer, deadPlayer, killerTeam, deadPlayerTeam);
        } else {
            // Death by environment/mob
            Text deathMessage = Text.literal(deadPlayer.getName().getString())
                .append(" (")
                .append(deadPlayerTeam.getFormattedName())
                .append(") died");
            
            if (deadPlayer.getServer() != null) {
                deadPlayer.getServer().getPlayerManager().broadcast(deathMessage, false);
            }
        }
    }
    
    private static void broadcastKillMessage(ServerPlayerEntity killer, TeamManager.Team killerTeam, 
                                           ServerPlayerEntity victim, TeamManager.Team victimTeam) {
        Text killMessage;
        
        if (killerTeam == victimTeam) {
            // Team kill
            killMessage = Text.literal(killer.getName().getString())
                .append(" (")
                .append(killerTeam.getFormattedName())
                .append(") killed teammate ")
                .append(victim.getName().getString())
                .formatted(Formatting.YELLOW);
        } else {
            // Enemy kill
            killMessage = Text.literal(killer.getName().getString())
                .append(" (")
                .append(killerTeam.getFormattedName())
                .append(") killed ")
                .append(victim.getName().getString())
                .append(" (")
                .append(victimTeam.getFormattedName())
                .append(")")
                .formatted(Formatting.GREEN);
        }
        
        if (killer.getServer() != null) {
            killer.getServer().getPlayerManager().broadcast(killMessage, false);
        }
    }
    
    private static void sendKillMessages(ServerPlayerEntity killer, ServerPlayerEntity victim, 
                                       TeamManager.Team killerTeam, TeamManager.Team victimTeam) {
        // Message to killer
        int killerKills = PlayerDataManager.getKills(killer.getUuid());
        Text killerMessage = Text.literal("You killed ")
            .append(victim.getName().getString())
            .append("! Total kills: " + killerKills)
            .formatted(Formatting.GREEN);
        killer.sendMessage(killerMessage, false);
        
        // Message to victim
        int victimDeaths = PlayerDataManager.getDeaths(victim.getUuid());
        Text victimMessage = Text.literal("You were killed by ")
            .append(killer.getName().getString())
            .append("! Total deaths: " + victimDeaths)
            .formatted(Formatting.RED);
        victim.sendMessage(victimMessage, false);
    }
    
    private static void incrementTeamKills(TeamManager.Team team) {
        teamKills.put(team, teamKills.get(team) + 1);
    }
    
    private static void incrementTeamDeaths(TeamManager.Team team) {
        teamDeaths.put(team, teamDeaths.get(team) + 1);
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
    
    public static void resetTeamStats() {
        for (TeamManager.Team team : TeamManager.Team.values()) {
            teamKills.put(team, 0);
            teamDeaths.put(team, 0);
        }
    }
    
    public static void resetPlayerStats(UUID playerId) {
        PlayerDataManager.PlayerData data = PlayerDataManager.getPlayerData(playerId);
        data.kills = 0;
        data.deaths = 0;
        PlayerDataManager.savePlayerData();
    }
}

