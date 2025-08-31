package com.kerpackie.lootgameshelper;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandSolve extends CommandBase {

    @Override
    public String getCommandName() {
        return "lgh_solve";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lgh_solve <NBT data from /blockdata command>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Accessible to all players on the client.
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
            return;
        }

        // Reconstruct the full NBT string from the command arguments
        String nbtString = String.join(" ", args);

        NBTParser.ParseResult result = NBTParser.parse(nbtString);

        sender.addChatMessage(new ChatComponentText(result.getMessage()));
    }
}
