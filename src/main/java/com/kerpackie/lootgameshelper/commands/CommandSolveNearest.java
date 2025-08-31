package com.kerpackie.lootgameshelper.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandSolveNearest extends CommandBase {

    private final CommandGetData getDataHandler = new CommandGetData();

    @Override
    public String getCommandName() {
        return "lgh_solve_nearest";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lgh_solve_nearest";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP Only
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("This command must be run by a player."));
            return;
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        int radius = 32;
        int yRadius = 5;

        TileEntity closestTile = null;
        double closestDistSq = -1.0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -yRadius; y <= yRadius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int currentX = (int) Math.floor(player.posX) + x;
                    int currentY = (int) Math.floor(player.posY) + y;
                    int currentZ = (int) Math.floor(player.posZ) + z;

                    TileEntity te = player.worldObj.getTileEntity(currentX, currentY, currentZ);
                    if (te != null) {
                        String teClassName = te.getClass()
                            .getName();
                        if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile")
                            || teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.MSMasterTile")) {

                            double distSq = player.getDistanceSq(currentX, currentY, currentZ);
                            if (closestTile == null || distSq < closestDistSq) {
                                closestTile = te;
                                closestDistSq = distSq;
                            }
                        }
                    }
                }
            }
        }

        if (closestTile != null) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + "Found closest game at "
                        + closestTile.xCoord
                        + ", "
                        + closestTile.yCoord
                        + ", "
                        + closestTile.zCoord
                        + ". Attempting to solve..."));
            // Reuse the existing logic from CommandGetData to handle the solving
            String teClassName = closestTile.getClass()
                .getName();
            if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile")) {
                getDataHandler.handleGameOfLight(player, closestTile);
            } else if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.MSMasterTile")) {
                getDataHandler.handleMinesweeper(player, closestTile);
            }
        } else {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.YELLOW + "No active LootGames found within " + radius + " blocks."));
        }
    }
}
