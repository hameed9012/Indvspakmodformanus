package com.manus.indvspak.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.manus.indvspak.TeamManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private static final String DATA_FILE = "indvspak_player_data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve(DATA_FILE);

    // This map stores all player data, making it accessible for other classes
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public static class PlayerData {
        public TeamManager.Team team;
        public int kills;
        public int deaths;
        public long lastSeen;

        public PlayerData() {
            this.team = TeamManager.Team.NEUTRAL;
            this.kills = 0;
            this.deaths = 0;
            this.lastSeen = System.currentTimeMillis();
        }

        public PlayerData(TeamManager.Team team, int kills, int deaths, long lastSeen) {
            this.team = team;
            this.kills = kills;
            this.deaths = deaths;
            this.lastSeen = lastSeen;
        }
    }

    public static void loadPlayerData() {
        if (!Files.exists(DATA_PATH)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(DATA_PATH)) {
            Type typeOfMap = new TypeToken<Map<UUID, PlayerData>>() {}.getType();
            Map<UUID, PlayerData> loadedData = GSON.fromJson(reader, typeOfMap);
            if (loadedData != null) {
                playerDataMap.putAll(loadedData);
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to load player data: " + e.getMessage());
        }
    }

    public static void savePlayerData() {
        try (Writer writer = Files.newBufferedWriter(DATA_PATH)) {
            GSON.toJson(playerDataMap, writer);
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to save player data: " + e.getMessage());
        }
    }

    public static PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, k -> new PlayerData());
    }

    // New method to get all player data for the leaderboard
    public static Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataMap);
    }

    public static void setPlayerTeam(UUID playerId, TeamManager.Team team) {
        PlayerData data = getPlayerData(playerId);
        data.team = team;
        data.lastSeen = System.currentTimeMillis();
    }

    public static TeamManager.Team getPlayerTeam(UUID playerId) {
        return getPlayerData(playerId).team;
    }

    public static void addKill(UUID playerId) {
        PlayerData data = getPlayerData(playerId);
        data.kills++;
        data.lastSeen = System.currentTimeMillis();
    }

    public static void addDeath(UUID playerId) {
        PlayerData data = getPlayerData(playerId);
        data.deaths++;
        data.lastSeen = System.currentTimeMillis();
    }

    public static int getKills(UUID playerId) {
        return getPlayerData(playerId).kills;
    }

    public static int getDeaths(UUID playerId) {
        return getPlayerData(playerId).deaths;
    }

    public static void updateLastSeen(UUID playerId) {
        PlayerData data = getPlayerData(playerId);
        data.lastSeen = System.currentTimeMillis();
    }

}
