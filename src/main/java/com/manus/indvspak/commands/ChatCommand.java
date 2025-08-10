package com.manus.indvspak.commands;

import com.manus.indvspak.chat.ChatManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Team chat command
        dispatcher.register(CommandManager.literal("tc")
            .then(CommandManager.argument("message", StringArgumentType.greedyString())
                .executes(ChatCommand::sendTeamMessage))
            .executes(ChatCommand::toggleTeamChat));
        
        // Alternative team chat command
        dispatcher.register(CommandManager.literal("teamchat")
            .then(CommandManager.argument("message", StringArgumentType.greedyString())
                .executes(ChatCommand::sendTeamMessage))
            .executes(ChatCommand::toggleTeamChat));
        
        // Global chat command (when team chat is enabled)
        dispatcher.register(CommandManager.literal("gc")
            .then(CommandManager.argument("message", StringArgumentType.greedyString())
                .executes(ChatCommand::sendGlobalMessage)));
        
        dispatcher.register(CommandManager.literal("globalchat")
            .then(CommandManager.argument("message", StringArgumentType.greedyString())
                .executes(ChatCommand::sendGlobalMessage)));
    }

    private static int sendTeamMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String message = StringArgumentType.getString(context, "message");
        
        ChatManager.sendTeamMessage(player, message);
        return 1;
    }

    private static int toggleTeamChat(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        ChatManager.toggleTeamChat(player);
        return 1;
    }

    private static int sendGlobalMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String message = StringArgumentType.getString(context, "message");
        
        ChatManager.sendGlobalMessage(player, message);
        return 1;
    }
}

