package com.manus.indvspak;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEventHandler {
    
    public static void register() {
        // Handle player join events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            
            // Assign team based on IP geolocation
            TeamManager.assignPlayerToTeam(player);
        });

        // Handle player disconnect events
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            // Could add cleanup logic here if needed
        });
    }
}

