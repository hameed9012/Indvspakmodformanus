package com.manus.indvspak.chat;

import com.manus.indvspak.TeamManager;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {
    
    // Players who have team chat enabled
    private static final Set<UUID> teamChatEnabled = new HashSet<>();
    
    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            // Modify chat message to include team prefix
            modifyChatMessage(message, sender);
        });
    }
    
    private static void modifyChatMessage(SignedMessage message, ServerPlayerEntity sender) {
        TeamManager.Team senderTeam = TeamManager.getPlayerTeam(sender.getUuid());
        
        // Create team prefix
        Text teamPrefix = Text.literal("[")
            .append(senderTeam.getFormattedName())
            .append("] ")
            .formatted(Formatting.BOLD);
        
        // Get original message content
        Text originalContent = message.getContent();
        
        // Create new message with team prefix
        Text newMessage = teamPrefix.copy().append(sender.getDisplayName()).append(": ").append(originalContent);
        
        // Broadcast the modified message to all players
        if (sender.getServer() != null) {
            sender.getServer().getPlayerManager().broadcast(newMessage, false);
        }
    }
    
    public static void sendTeamMessage(ServerPlayerEntity sender, String messageContent) {
        TeamManager.Team senderTeam = TeamManager.getPlayerTeam(sender.getUuid());
        
        // Create team chat prefix
        Text teamChatPrefix = Text.literal("[TEAM] ")
            .formatted(Formatting.BOLD)
            .formatted(senderTeam.getColor());
        
        // Create the full team message
        Text teamMessage = teamChatPrefix.copy()
            .append(sender.getDisplayName())
            .append(": ")
            .append(Text.literal(messageContent));
        
        // Send to all team members
        if (sender.getServer() != null) {
            for (ServerPlayerEntity player : sender.getServer().getPlayerManager().getPlayerList()) {
                TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
                if (playerTeam == senderTeam) {
                    player.sendMessage(teamMessage, false);
                }
            }
        }
    }
    
    public static void sendGlobalMessage(ServerPlayerEntity sender, String messageContent) {
        TeamManager.Team senderTeam = TeamManager.getPlayerTeam(sender.getUuid());
        
        // Create team prefix for global chat
        Text teamPrefix = Text.literal("[")
            .append(senderTeam.getFormattedName())
            .append("] ")
            .formatted(Formatting.BOLD);
        
        // Create the full global message
        Text globalMessage = teamPrefix.copy()
            .append(sender.getDisplayName())
            .append(": ")
            .append(Text.literal(messageContent));
        
        // Broadcast to all players
        if (sender.getServer() != null) {
            sender.getServer().getPlayerManager().broadcast(globalMessage, false);
        }
    }
    
    public static boolean isTeamChatEnabled(UUID playerId) {
        return teamChatEnabled.contains(playerId);
    }
    
    public static void setTeamChatEnabled(UUID playerId, boolean enabled) {
        if (enabled) {
            teamChatEnabled.add(playerId);
        } else {
            teamChatEnabled.remove(playerId);
        }
    }
    
    public static void toggleTeamChat(ServerPlayerEntity player) {
        boolean currentState = isTeamChatEnabled(player.getUuid());
        setTeamChatEnabled(player.getUuid(), !currentState);
        
        Text message;
        if (!currentState) {
            message = Text.literal("Team chat enabled. Your messages will only be visible to your team.")
                .formatted(Formatting.GREEN);
        } else {
            message = Text.literal("Team chat disabled. Your messages will be visible to everyone.")
                .formatted(Formatting.YELLOW);
        }
        
        player.sendMessage(message, false);
    }
    
    public static void sendTeamAnnouncement(TeamManager.Team team, String announcement) {
        Text announcementMessage = Text.literal("[TEAM ANNOUNCEMENT] ")
            .formatted(Formatting.BOLD)
            .formatted(Formatting.GOLD)
            .append(Text.literal(announcement).formatted(team.getColor()));
        
        // Send to all team members (assuming we have access to server)
        // This would need to be called from a context where we have server access
    }
    
    public static void sendServerAnnouncement(String announcement) {
        Text announcementMessage = Text.literal("[SERVER] ")
            .formatted(Formatting.BOLD)
            .formatted(Formatting.DARK_PURPLE)
            .append(Text.literal(announcement).formatted(Formatting.WHITE));
        
        // This would need to be called from a context where we have server access
    }
}

