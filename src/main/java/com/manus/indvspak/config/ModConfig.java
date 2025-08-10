package com.manus.indvspak.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final String CONFIG_FILE = "indvspak_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);

    // Configuration settings
    public boolean enableIPDetection = true;
    public boolean enableTeamChat = true;
    public boolean enableKillTracking = true;
    public boolean enableProtection = true;
    public boolean broadcastTeamSwitches = true;
    public boolean broadcastKills = true;
    public boolean allowNeutralTeam = true;
    public boolean persistPlayerData = true;
    
    // Team settings
    public String indiaTeamName = "India";
    public String pakistanTeamName = "Pakistan";
    public String neutralTeamName = "Neutral";
    
    // Chat settings
    public String teamChatPrefix = "[TEAM]";
    public String globalChatPrefix = "";
    public boolean showTeamPrefixInChat = true;
    
    // Protection settings
    public boolean allowTeammateAccess = true;
    public boolean allowNeutralAccess = false;
    public boolean protectFromTeammates = true;
    
    // Kill tracking settings
    public boolean trackTeamKills = false; // Don't count team kills towards stats
    public boolean announceFirstBlood = true;
    public boolean announceKillStreaks = true;
    public int killStreakThreshold = 5;
    
    // GeoIP settings
    public String geoipDatabasePath = "/assets/indiavspakistanmod/GeoLite2-Country.mmdb";
    public boolean fallbackToNeutral = true;
    
    private static ModConfig instance;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    private static ModConfig loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                ModConfig config = GSON.fromJson(json, ModConfig.class);
                System.out.println("[IndiaVsPakistanMod] Configuration loaded successfully");
                return config;
            } else {
                ModConfig config = new ModConfig();
                config.saveConfig();
                System.out.println("[IndiaVsPakistanMod] Created default configuration file");
                return config;
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to load configuration: " + e.getMessage());
            return new ModConfig();
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
            System.out.println("[IndiaVsPakistanMod] Configuration saved successfully");
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to save configuration: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        instance = loadConfig();
    }

    // Getters for easy access
    public boolean isIPDetectionEnabled() {
        return enableIPDetection;
    }

    public boolean isTeamChatEnabled() {
        return enableTeamChat;
    }

    public boolean isKillTrackingEnabled() {
        return enableKillTracking;
    }

    public boolean isProtectionEnabled() {
        return enableProtection;
    }

    public boolean shouldBroadcastTeamSwitches() {
        return broadcastTeamSwitches;
    }

    public boolean shouldBroadcastKills() {
        return broadcastKills;
    }

    public boolean isNeutralTeamAllowed() {
        return allowNeutralTeam;
    }

    public boolean shouldPersistPlayerData() {
        return persistPlayerData;
    }

    public String getTeamChatPrefix() {
        return teamChatPrefix;
    }

    public String getGlobalChatPrefix() {
        return globalChatPrefix;
    }

    public boolean shouldShowTeamPrefixInChat() {
        return showTeamPrefixInChat;
    }

    public boolean shouldAllowTeammateAccess() {
        return allowTeammateAccess;
    }

    public boolean shouldAllowNeutralAccess() {
        return allowNeutralAccess;
    }

    public boolean shouldProtectFromTeammates() {
        return protectFromTeammates;
    }

    public boolean shouldTrackTeamKills() {
        return trackTeamKills;
    }

    public boolean shouldAnnounceFirstBlood() {
        return announceFirstBlood;
    }

    public boolean shouldAnnounceKillStreaks() {
        return announceKillStreaks;
    }

    public int getKillStreakThreshold() {
        return killStreakThreshold;
    }

    public String getGeoipDatabasePath() {
        return geoipDatabasePath;
    }

    public boolean shouldFallbackToNeutral() {
        return fallbackToNeutral;
    }
}

