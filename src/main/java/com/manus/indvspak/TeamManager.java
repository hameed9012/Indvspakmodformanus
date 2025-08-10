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
import java.net.SocketAddress;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
            return Text.literal(this.displayName).formatted(this.color);
        }

        public static List<String> getNames() {
            return Arrays.stream(Team.values())
                    .map(team -> team.getDisplayName().toLowerCase())
                    .collect(Collectors.toList());
        }

        public static Team fromName(String name) {
            for (Team team : Team.values()) {
                if (team.getDisplayName().equalsIgnoreCase(name)) {
                    return team;
                }
            }
            return null;
        }
    }

    // GeoIP2 database reader
    private static DatabaseReader geoReader;
    private static CompletableFuture<Void> geoReaderFuture;

    public static void initialize() {
        // Load the GeoLite2-Country database asynchronously to not block the server thread
        geoReaderFuture = CompletableFuture.runAsync(() -> {
            try (InputStream database = TeamManager.class.getResourceAsStream("/assets/indiavspakistanmod/GeoLite2-Country.mmdb")) {
                if (database != null) {
                    geoReader = new DatabaseReader.Builder(database).build();
                    System.out.println("[IndiaVsPakistanMod] GeoLite2-Country database loaded successfully.");
                } else {
                    System.err.println("[IndiaVsPakistanMod] GeoLite2-Country.mmdb not found in assets. IP-based team assignment will not work.");
                }
            } catch (IOException e) {
                System.err.println("[IndiaVsPakistanMod] Failed to load GeoLite2-Country database: " + e.getMessage());
            }
        });
    }

    public static Team getPlayerTeam(UUID playerId) {
        return PlayerDataManager.getPlayerData(playerId).team;
    }

    public static void setPlayerTeam(UUID playerId, Team team) {
        PlayerDataManager.setPlayerTeam(playerId, team);
    }

    public static void assignPlayerToTeam(ServerPlayerEntity player) {
        // Fixed server access issue - use getServer() method instead of direct field access
        if (player.getServer() == null || !player.getServer().isDedicated() || geoReader == null || !geoReaderFuture.isDone()) {
            return;
        }

        // Do not assign a team if one is already assigned
        if (PlayerDataManager.getPlayerData(player.getUuid()).team != Team.NEUTRAL) {
            return;
        }

        // Fixed connection access issue - use getConnection() method
        SocketAddress socketAddress = player.networkHandler.getConnection().getAddress();
        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        InetSocketAddress address = (InetSocketAddress) socketAddress;
        InetAddress inetAddress = address.getAddress();
        if (inetAddress == null) return;

        try {
            CountryResponse response = geoReader.country(inetAddress);
            String countryCode = response.getCountry().getIsoCode();

            if (countryCode.equals(Team.INDIA.getCountryCode())) {
                setPlayerTeam(player.getUuid(), Team.INDIA);
                sendTeamAssignmentMessage(player, Team.INDIA);
            } else if (countryCode.equals(Team.PAKISTAN.getCountryCode())) {
                setPlayerTeam(player.getUuid(), Team.PAKISTAN);
                sendTeamAssignmentMessage(player, Team.PAKISTAN);
            } else {
                setPlayerTeam(player.getUuid(), Team.NEUTRAL);
                sendTeamAssignmentMessage(player, Team.NEUTRAL);
            }
        } catch (IOException | GeoIp2Exception e) {
            System.err.println("[IndiaVsPakistanMod] Failed to lookup IP address: " + e.getMessage());
        }
    }

    private static void sendTeamAssignmentMessage(ServerPlayerEntity player, Team assignedTeam) {
        Text message = Text.literal("You have been automatically assigned to team: ").append(assignedTeam.getFormattedName());
        player.sendMessage(message, false);

        Text broadcastMessage = Text.literal(player.getName().getString() + " has joined team ").append(assignedTeam.getFormattedName());
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