package com.kerpackie.lootgameshelper.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandScan extends CommandBase {

    @Override
    public String getCommandName() {
        return "lgh_scan";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lgh_scan";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("This command must be run by a player."));
            return;
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Scanning for LootGames..."));

        int radius = 32;
        int verticalRange = 5;
        int foundCount = 0;

        int playerX = (int) Math.floor(player.posX);
        int playerY = (int) Math.floor(player.posY);
        int playerZ = (int) Math.floor(player.posZ);

        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - verticalRange; y <= playerY + verticalRange; y++) {
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    TileEntity te = player.worldObj.getTileEntity(x, y, z);

                    if (te != null) {
                        String teClassName = te.getClass()
                            .getName();
                        String gameName = null;

                        if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile")) {
                            gameName = "Game of Light";
                        } else if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.MSMasterTile")) {
                            gameName = "Minesweeper";
                        }

                        if (gameName != null) {
                            foundCount++;
                            String message = String
                                .format("%s Found %s at: %d, %d, %d", EnumChatFormatting.GREEN, gameName, x, y, z);
                            player.addChatMessage(new ChatComponentText(message));
                        }
                    }
                }
            }
        }

        if (foundCount == 0) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "No active LootGames found in the area."));
        } else {
            String message = String.format("%s Scan complete. Found %d game(s).", EnumChatFormatting.AQUA, foundCount);
            player.addChatMessage(new ChatComponentText(message));
        }
    }
}
