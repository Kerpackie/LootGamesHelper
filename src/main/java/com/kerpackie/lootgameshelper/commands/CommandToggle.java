package com.kerpackie.lootgameshelper.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.kerpackie.lootgameshelper.ClientCache;

public class CommandToggle extends CommandBase {

    @Override
    public String getCommandName() {
        return "lgh_toggle";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lgh_toggle";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Client-side command, no permissions needed.
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // Ensures it can be run client-side
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ClientCache.isOverlayEnabled = !ClientCache.isOverlayEnabled;

        String status = ClientCache.isOverlayEnabled ? EnumChatFormatting.GREEN + "ENABLED"
            : EnumChatFormatting.RED + "DISABLED";

        sender.addChatMessage(new ChatComponentText("LootGames Helper overlay is now " + status));
    }
}
