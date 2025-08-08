package com.manus.indvspak.mixin;

import com.manus.indvspak.TeamManager;
import com.manus.indvspak.chat.ChatManager;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    
    @Shadow
    public ServerPlayerEntity player;
    
    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(SignedMessage message, CallbackInfo ci) {
        // Check if player has team chat enabled
        if (ChatManager.isTeamChatEnabled(player.getUuid())) {
            // Send as team message instead of global
            ChatManager.sendTeamMessage(player, message.getContent().getString());
            ci.cancel(); // Cancel the original message
            return;
        }
        
        // For global messages, add team prefix
        TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
        
        // Create team prefix
        Text teamPrefix = Text.literal("[")
            .append(playerTeam.getFormattedName())
            .append("] ")
            .formatted(Formatting.BOLD);
        
        // Create new message with team prefix
        Text newMessage = teamPrefix.copy()
            .append(player.getDisplayName())
            .append(": ")
            .append(message.getContent());
        
        // Broadcast the modified message
        if (player.getServer() != null) {
            player.getServer().getPlayerManager().broadcast(newMessage, false);
        }
        
        // Cancel the original message to prevent duplicate
        ci.cancel();
    }
}

