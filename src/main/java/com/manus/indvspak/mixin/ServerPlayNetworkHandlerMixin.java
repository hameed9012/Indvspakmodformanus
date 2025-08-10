package com.manus.indvspak.mixin;

import com.manus.indvspak.chat.ChatManager;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    // NOTE: This mixin is now disabled because all chat logic is handled in ChatManager.java.
    // The ServerMessageEvents.CHAT_MESSAGE event handler is the standard way to handle chat
    // and is more reliable than a mixin on handleDecoratedMessage.

    /*
    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(SignedMessage message, CallbackInfo ci) {
        // The chat manager now handles all logic through the ServerMessageEvents API.
        // We no longer need to cancel or modify the message here.
        // The code is left here as a comment for reference.
        // ChatManager.handlePlayerChatMessage(player, message.getContent().getString());
        // ci.cancel();
    }
    */
}
