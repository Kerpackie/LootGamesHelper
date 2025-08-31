package com.kerpackie.lootgameshelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.EnumChatFormatting;

public class NBTParser {

    // Regex to find coordinates like x:123, y:64, z:-456
    private static final Pattern COORD_PATTERN = Pattern.compile("x:(-?\\d+),y:(-?\\d+),z:(-?\\d+)");

    // Regex to find a Game of Light sequence, e.g., sequence:[I;1,2,3,4,5]
    private static final Pattern GOL_SEQUENCE_PATTERN = Pattern.compile("sequence:\\[I;([\\d,]+)\\]");

    // Regex to find a Minesweeper board, e.g., board:[B;1b,2b,...,9b]
    private static final Pattern MS_BOARD_PATTERN = Pattern.compile("board:\\[B;([\\db,s-]+)\\]");
    // Regex to find the board size, e.g., board_size:9
    private static final Pattern MS_SIZE_PATTERN = Pattern.compile("board_size:(\\d+)");

    public static ParseResult parse(String nbtString) {
        Matcher coordMatcher = COORD_PATTERN.matcher(nbtString);
        if (!coordMatcher.find()) {
            return new ParseResult(EnumChatFormatting.RED + "Could not find coordinates in the NBT data.");
        }

        try {
            int x = Integer.parseInt(coordMatcher.group(1));
            int y = Integer.parseInt(coordMatcher.group(2));
            int z = Integer.parseInt(coordMatcher.group(3));

            // Try parsing as Game of Light
            Matcher golMatcher = GOL_SEQUENCE_PATTERN.matcher(nbtString);
            if (golMatcher.find()) {
                String[] sequenceStrings = golMatcher.group(1)
                    .split(",");
                int[] sequence = new int[sequenceStrings.length];
                for (int i = 0; i < sequenceStrings.length; i++) {
                    sequence[i] = Integer.parseInt(sequenceStrings[i]);
                }
                ClientCache.updateGoLData(sequence, x, y, z);
                return new ParseResult(EnumChatFormatting.GREEN + "Game of Light solution loaded!");
            }

            // Try parsing as Minesweeper
            Matcher msBoardMatcher = MS_BOARD_PATTERN.matcher(nbtString);
            Matcher msSizeMatcher = MS_SIZE_PATTERN.matcher(nbtString);

            if (msBoardMatcher.find() && msSizeMatcher.find()) {
                int size = Integer.parseInt(msSizeMatcher.group(1));
                String[] boardBytesStr = msBoardMatcher.group(1)
                    .replace("b", "")
                    .split(",");
                if (boardBytesStr.length != size * size) {
                    return new ParseResult(
                        EnumChatFormatting.RED + "Minesweeper board data is corrupt. Expected "
                            + (size * size)
                            + " fields, found "
                            + boardBytesStr.length
                            + ".");
                }

                byte[][] board = new byte[size][size];
                for (int i = 0; i < boardBytesStr.length; i++) {
                    int row = i / size;
                    int col = i % size;
                    board[col][row] = Byte.parseByte(boardBytesStr[i]);
                }

                ClientCache.updateMSData(board, x, y, z, size);
                return new ParseResult(EnumChatFormatting.GREEN + "Minesweeper solution loaded!");
            }

            return new ParseResult(EnumChatFormatting.YELLOW + "Could not find a valid game solution in the NBT data.");

        } catch (NumberFormatException e) {
            LootGamesHelper.logger.error("Failed to parse number from NBT string", e);
            return new ParseResult(
                EnumChatFormatting.RED + "Error parsing numbers from NBT data. Check log for details.");
        } catch (Exception e) {
            LootGamesHelper.logger.error("An unexpected error occurred during NBT parsing", e);
            return new ParseResult(EnumChatFormatting.RED + "An unexpected error occurred. Check log for details.");
        }
    }

    public static class ParseResult {

        private final String message;

        public ParseResult(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
