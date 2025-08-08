package com.manus.indvspak;

import com.manus.indvspak.commands.AdminCommand;
import com.manus.indvspak.commands.ChatCommand;
import com.manus.indvspak.commands.ConfigCommand;
import com.manus.indvspak.commands.HelpCommand;
import com.manus.indvspak.commands.RegionCommand;
import com.manus.indvspak.commands.StatsCommand;
import com.manus.indvspak.commands.TeamCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandRegistry {
    
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TeamCommand.register(dispatcher);
            AdminCommand.register(dispatcher);
            RegionCommand.register(dispatcher);
            StatsCommand.register(dispatcher);
            ChatCommand.register(dispatcher);
            ConfigCommand.register(dispatcher);
            HelpCommand.register(dispatcher);
        });
    }
}

