package com.manus.indvspak

import com.manus.indvspak.chat.ChatManager
import com.manus.indvspak.config.ModConfig
import com.manus.indvspak.protection.ProtectionEventHandler
import com.manus.indvspak.protection.RegionManager
import com.manus.indvspak.stats.KillTracker
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object IndiaVsPakistanMod : ModInitializer {
    private val logger = LoggerFactory.getLogger("indiavspakistanmod")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Initializing India vs Pakistan Mod...")
		
		// Initialize configuration
		ModConfig.getInstance()
		
		// Initialize team management system
		TeamManager.initialize()
		
		// Initialize protection system
		RegionManager.loadRegions()
		
		// Register event handlers
		PlayerEventHandler.register()
		ProtectionEventHandler.register()
		KillTracker.register()
		ChatManager.register()
		
		// Register commands
		CommandRegistry.register()
		
		logger.info("India vs Pakistan Mod initialized successfully!")
	}
}