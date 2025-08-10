package com.manus.indvspak;

import com.manus.indvspak.commands.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegistry {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            IndiaVsPakistanMod.LOGGER.info("Registering commands...");

            try {
                TeamCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("TeamCommand registered");

                AdminCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("AdminCommand registered");

                RegionCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("RegionCommand registered");

                StatsCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("StatsCommand registered");

                ChatCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("ChatCommand registered");

                ConfigCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("ConfigCommand registered");

                HelpCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("HelpCommand registered");

                DebugCommand.register(dispatcher);
                IndiaVsPakistanMod.LOGGER.info("DebugCommand registered");

                IndiaVsPakistanMod.LOGGER.info("All commands registered successfully!");

            } catch (Exception e) {
                IndiaVsPakistanMod.LOGGER.error("Failed to register commands", e);
            }
        });
    }
}