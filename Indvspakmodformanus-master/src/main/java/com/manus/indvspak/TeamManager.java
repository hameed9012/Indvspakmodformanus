package com.manus.indvspak;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.manus.indvspak.data.PlayerDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

public class TeamManager {
    public enum Team {
        INDIA("India", Formatting.GREEN, "IN"),
        PAKISTAN("Pakistan", Formatting.RED, "PK"),
        NEUTRAL("Neutral", Formatting.GRAY, "");

        private final String displayName;
        private final Formatting color;
        private final String countryCode;

        Team(String displayName, Formatting color, String countryCode) {
            this.displayName = displayName;
            this.color = color;
            this.countryCode = countryCode;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Formatting getColor() {
            return color;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public Text getFormattedName() {
            return Text.literal(displayName).formatted(color);
        }
    }

    private static DatabaseReader geoReader;

    public static void initialize() {
        // Load persistent player data
        PlayerDataManager.loadPlayerData();
        
        try {
            // Try to load GeoLite2 database from resources
            InputStream dbStream = TeamManager.class.getResourceAsStream("/assets/indiavspakistanmod/GeoLite2-Country.mmdb");
            if (dbStream != null) {
                geoReader = new DatabaseReader.Builder(dbStream).build();
                System.out.println("[IndiaVsPakistanMod] GeoIP2 database loaded successfully");
            } else {
                System.err.println("[IndiaVsPakistanMod] GeoIP2 database not found in resources");
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to load GeoIP2 database: " + e.getMessage());
        }
    }

    public static Team getPlayerTeam(UUID playerId) {
        return PlayerDataManager.getPlayerTeam(playerId);
    }

    public static void setPlayerTeam(UUID playerId, Team team) {
        PlayerDataManager.setPlayerTeam(playerId, team);
        // Save data periodically
        PlayerDataManager.savePlayerData();
    }

    public static Team detectTeamFromIP(ServerPlayerEntity player) {
        // For now, return NEUTRAL for all players
        // IP detection can be implemented later with proper access to player IP
        // This requires either reflection or a mixin to access protected fields
        System.out.println("[IndiaVsPakistanMod] IP detection not implemented - assigning NEUTRAL team");
        return Team.NEUTRAL;
    }

    public static void assignPlayerToTeam(ServerPlayerEntity player) {
        // Check if player already has a team assigned
        Team existingTeam = getPlayerTeam(player.getUuid());
        if (existingTeam != Team.NEUTRAL) {
            // Player already has a team, just welcome them back
            Text welcomeMessage = Text.literal("Welcome back! You are on team ")
                    .append(existingTeam.getFormattedName());
            player.sendMessage(welcomeMessage, false);
            return;
        }

        // Detect team from IP for new players
        Team detectedTeam = detectTeamFromIP(player);
        setPlayerTeam(player.getUuid(), detectedTeam);

        // Send welcome message to player
        Text welcomeMessage = Text.literal("Welcome! You have been assigned to team: ")
                .append(detectedTeam.getFormattedName());
        player.sendMessage(welcomeMessage, false);

        // Broadcast team assignment to all players
        Text broadcastMessage = Text.literal(player.getName().getString() + " has joined team ")
                .append(detectedTeam.getFormattedName());
        
        if (player.getServer() != null) {
            player.getServer().getPlayerManager().broadcast(broadcastMessage, false);
        }
    }

    public static boolean switchPlayerTeam(ServerPlayerEntity player, Team newTeam) {
        Team currentTeam = getPlayerTeam(player.getUuid());
        
        if (currentTeam == newTeam) {
            player.sendMessage(Text.literal("You are already on team ").append(newTeam.getFormattedName()), false);
            return false;
        }

        setPlayerTeam(player.getUuid(), newTeam);
        
        Text switchMessage = Text.literal("You have switched to team ").append(newTeam.getFormattedName());
        player.sendMessage(switchMessage, false);

        // Broadcast team switch to all players
        Text broadcastMessage = Text.literal(player.getName().getString() + " has switched to team ")
                .append(newTeam.getFormattedName());
        
        if (player.getServer() != null) {
            player.getServer().getPlayerManager().broadcast(broadcastMessage, false);
        }

        return true;
    }

    public static void cleanup() {
        // Save player data before shutdown
        PlayerDataManager.savePlayerData();
        
        if (geoReader != null) {
            try {
                geoReader.close();
            } catch (IOException e) {
                System.err.println("[IndiaVsPakistanMod] Failed to close GeoIP2 database: " + e.getMessage());
            }
        }
    }
}

