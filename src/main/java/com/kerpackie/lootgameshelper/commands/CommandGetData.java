package com.kerpackie.lootgameshelper.commands;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.kerpackie.lootgameshelper.LootGamesHelper;
import com.kerpackie.lootgameshelper.PacketGoLData;
import com.kerpackie.lootgameshelper.PacketMSData;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.common.config.ConfigMS;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;
import ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard;

public class CommandGetData extends CommandBase {

    // Cache reflection fields for performance
    private Field masterGameField;
    private Field golStageField;
    private Field golSequenceField;

    private Field msGameFieldBoard;
    private Field msBoardArrayField;
    private Field msFieldTypeField;
    private Field msBoardSizeField;
    private Field msConfigSnapshotField;

    @Override
    public String getCommandName() {
        return "lgh_get";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lgh_get <x> <y> <z>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allow all players to use this command
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("This command must be run by a player."));
            return;
        }

        if (args.length != 3) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Invalid arguments. " + getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        try {
            int x = parseInt(sender, args[0]);
            int y = parseInt(sender, args[1]);
            int z = parseInt(sender, args[2]);

            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te == null) {
                player.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.RED + "No Tile Entity found at that location."));
                return;
            }

            String teClassName = te.getClass()
                .getName();
            if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile")) {
                handleGameOfLight(player, te);
            } else if (teClassName.equals("ru.timeconqueror.lootgames.common.block.tile.MSMasterTile")) {
                handleMinesweeper(player, te);
            } else {
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.RED
                            + "The block at that location is not a recognized LootGame Master Tile."));
            }

        } catch (Exception e) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED
                        + "An error occurred while processing the command. Check the server console."));
            LootGamesHelper.logger.error("Failed to execute /lgh_get command.", e);
        }
    }

    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // Field not in this class, try the superclass
            }
            current = current.getSuperclass();
        }
        throw new NoSuchFieldException(
            "Field '" + fieldName + "' not found in class " + clazz.getName() + " or any of its superclasses.");
    }

    private void handleGameOfLight(EntityPlayerMP player, TileEntity masterTile) {
        try {
            if (masterGameField == null) {
                masterGameField = findField(masterTile.getClass(), "game");
            }
            Object gameInstance = masterGameField.get(masterTile);
            if (gameInstance == null) return;

            if (golStageField == null) {
                golStageField = LootGame.class.getDeclaredField("stage");
                golStageField.setAccessible(true);
            }
            Object stage = golStageField.get(gameInstance);

            if (stage != null) {
                String stageName = stage.getClass()
                    .getSimpleName();
                LootGamesHelper.logger.info("Found Game of Light in stage: " + stageName);

                if (stageName.equals("StageWaitingForSequence") || stageName.equals("StageShowSequence")) {
                    if (golSequenceField == null) {
                        golSequenceField = findField(stage.getClass(), "sequence");
                    }
                    List<Symbol> sequenceList = (List<Symbol>) golSequenceField.get(stage);
                    if (sequenceList == null || sequenceList.isEmpty()) {
                        player.addChatMessage(
                            new ChatComponentText(
                                EnumChatFormatting.RED + "Found the game, but the sequence is empty."));
                        return;
                    }

                    int[] sequenceIndices = sequenceList.stream()
                        .mapToInt(Symbol::getIndex)
                        .toArray();
                    LootGamesHelper.network.sendTo(
                        new PacketGoLData(sequenceIndices, masterTile.xCoord, masterTile.yCoord, masterTile.zCoord),
                        player);
                    player.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.GREEN + "Game of Light solution sent!"));
                } else {
                    player.addChatMessage(
                        new ChatComponentText(
                            EnumChatFormatting.YELLOW
                                + "Game of Light is not in the right stage to get a solution. Current stage: "
                                + stageName));
                }
            } else {
                player.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.YELLOW + "Game of Light stage is currently null."));
            }
        } catch (Exception e) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Failed to get Game of Light solution via reflection."));
            LootGamesHelper.logger.error("Reflection failed for GameOfLight.", e);
        }
    }

    private void handleMinesweeper(EntityPlayerMP player, TileEntity masterTile) {
        try {
            if (masterGameField == null) {
                masterGameField = findField(masterTile.getClass(), "game");
            }
            Object gameInstance = masterGameField.get(masterTile);
            if (gameInstance == null) return;

            if (msGameFieldBoard == null) {
                msGameFieldBoard = findField(gameInstance.getClass(), "board");
            }
            Object boardObject = msGameFieldBoard.get(gameInstance);
            if (boardObject == null) return;

            boolean isGenerated = (boolean) boardObject.getClass()
                .getMethod("isGenerated")
                .invoke(boardObject);
            if (!isGenerated) {
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.YELLOW
                            + "Minesweeper board has not been generated yet. Click a tile first!"));
                return;
            }

            if (msBoardSizeField == null) {
                msBoardSizeField = MSBoard.class.getDeclaredField("size");
                msBoardSizeField.setAccessible(true);
            }
            int boardSize = (int) msBoardSizeField.get(boardObject);

            // Get allocated size for centering calculations
            if (msConfigSnapshotField == null) {
                msConfigSnapshotField = findField(gameInstance.getClass(), "configSnapshot");
            }
            ConfigMS.Snapshot configSnapshot = (ConfigMS.Snapshot) msConfigSnapshotField.get(gameInstance);
            int allocatedSize = configSnapshot.getStage4()
                .getBoardSize();

            if (msBoardArrayField == null) {
                msBoardArrayField = MSBoard.class.getDeclaredField("board");
                msBoardArrayField.setAccessible(true);
            }
            Object[][] boardArray = (Object[][]) msBoardArrayField.get(boardObject);

            if (msFieldTypeField == null) {
                Class<?> msFieldClass = Class
                    .forName("ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard$MSField");
                msFieldTypeField = msFieldClass.getDeclaredField("type");
                msFieldTypeField.setAccessible(true);
            }

            byte[][] boardData = new byte[boardSize][boardSize];
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    Object field = boardArray[x][y];
                    Enum<?> type = (Enum<?>) msFieldTypeField.get(field);
                    boardData[x][y] = (byte) type.ordinal();
                }
            }

            LootGamesHelper.network.sendTo(
                new PacketMSData(
                    boardData,
                    masterTile.xCoord,
                    masterTile.yCoord,
                    masterTile.zCoord,
                    boardSize,
                    allocatedSize),
                player);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Minesweeper solution sent!"));
        } catch (Exception e) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Failed to get Minesweeper solution via reflection."));
            LootGamesHelper.logger.error("Reflection failed for Minesweeper.", e);
        }
    }
}
