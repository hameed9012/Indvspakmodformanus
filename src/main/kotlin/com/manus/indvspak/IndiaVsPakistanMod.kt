package com.manus.indvspak;

import com.manus.indvspak.chat.ChatManager;
import com.manus.indvspak.config.ModConfig;
import com.manus.indvspak.data.PlayerDataManager;
import com.manus.indvspak.protection.ProtectionEventHandler;
import com.manus.indvspak.protection.RegionManager;
import com.manus.indvspak.stats.KillTracker;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndiaVsPakistanMod implements ModInitializer {
    public static final String MOD_ID = "indiavspakistanmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Starting India vs Pakistan Mod initialization...");

        try {
            // Load configuration first
            ModConfig.getInstance();
            LOGGER.info("Configuration loaded successfully");

            // Initialize team manager and GeoIP database
            TeamManager.initialize();
            LOGGER.info("Team manager initialized");

            // Load player data
            PlayerDataManager.loadPlayerData();
            LOGGER.info("Player data loaded");

            // Load regions
            RegionManager.loadRegions();
            LOGGER.info("Regions loaded");

            // Register event handlers
            PlayerEventHandler.register();
            LOGGER.info("Player event handler registered");

            ProtectionEventHandler.register();
            LOGGER.info("Protection event handler registered");

            KillTracker.register();
            LOGGER.info("Kill tracker registered");

            ChatManager.register();
            LOGGER.info("Chat manager registered");

            // Register commands
            CommandRegistry.register();
            LOGGER.info("Commands registered");

            LOGGER.info("India vs Pakistan Mod initialized successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize India vs Pakistan Mod", e);
            throw new RuntimeException("Mod initialization failed", e);
        }
    }
}