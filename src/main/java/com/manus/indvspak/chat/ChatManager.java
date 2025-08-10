package com.manus.indvspak.chat;

import com.manus.indvspak.TeamManager;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {

    // Players who have team chat enabled
    private static final Set<UUID> teamChatEnabled = new HashSet<>();

    public static void register() {
        // Register a handler for when a chat message is received from a player.
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            // Get the original message content
            String messageContent = message.getContent().getString();

            // Check if the player has team chat enabled.
            if (isTeamChatEnabled(sender.getUuid())) {
                // If team chat is enabled, send the message only to the player's teammates.
                sendTeamMessage(sender, messageContent);
            } else {
                // If team chat is not enabled, process it as a global message.
                sendGlobalMessage(sender, messageContent);
            }

            // Return ALLOW to let the event continue processing (or DENY to cancel)
            return ServerMessageEvents.ChatMessage.Result.ALLOW;
        });
    }

    public static boolean isTeamChatEnabled(UUID playerUuid) {
        return teamChatEnabled.contains(playerUuid);
    }

    public static void setTeamChatEnabled(UUID playerUuid, boolean enabled) {
        if (enabled) {
            teamChatEnabled.add(playerUuid);
        } else {
            teamChatEnabled.remove(playerUuid);
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

    public static void sendTeamMessage(ServerPlayerEntity sender, String messageContent) {
        // Get the sender's team.
        TeamManager.Team senderTeam = TeamManager.getPlayerTeam(sender.getUuid());

        // Create the formatted message with a team prefix.
        MutableText message = Text.literal("[TEAM] ").formatted(Formatting.BOLD, Formatting.YELLOW)
                .append(senderTeam.getFormattedName())
                .append(Text.literal(" " + sender.getName().getString() + ": ").formatted(Formatting.RESET))
                .append(Text.literal(messageContent).formatted(Formatting.WHITE));

        // Send the message to all players on the same team as the sender.
        if (sender.getServer() != null) {
            for (ServerPlayerEntity player : sender.getServer().getPlayerManager().getPlayerList()) {
                if (TeamManager.getPlayerTeam(player.getUuid()) == senderTeam) {
                    player.sendMessage(message, false);
                }
            }
        }
    }

    public static void sendGlobalMessage(ServerPlayerEntity sender, String messageContent) {
        // Get the sender's team.
        TeamManager.Team senderTeam = TeamManager.getPlayerTeam(sender.getUuid());

        // Create a formatted global message with team prefix
        MutableText message = Text.literal("")
                .append(senderTeam.getFormattedName())
                .append(Text.literal(" " + sender.getName().getString() + ": ").formatted(Formatting.RESET))
                .append(Text.literal(messageContent).formatted(Formatting.WHITE));

        // Broadcast the message to all players.
        if (sender.getServer() != null) {
            sender.getServer().getPlayerManager().broadcast(message, false);
        }
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