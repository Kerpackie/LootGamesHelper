package com.kerpackie.lootgameshelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientCache {

    // The maximum distance the player can be from the game before the overlay disappears.
    // We use the squared distance for a more efficient check (32*32 = 1024).
    private static final double MAX_DISTANCE_SQ = 1024.0;

    // --- Game of Light Data ---
    public static int[] golSequence = null;
    public static int golX, golY, golZ;

    // --- Minesweeper Data ---
    public static byte[][] msBoard = null;
    public static int msX, msY, msZ, msSize;

    public static void updateGoLData(int[] sequence, int x, int y, int z) {
        golSequence = sequence;
        golX = x;
        golY = y;
        golZ = z;
    }

    public static void updateMSData(byte[][] board, int x, int y, int z, int size) {
        msBoard = board;
        msX = x;
        msY = y;
        msZ = z;
        msSize = size;
    }

    /**
     * Clears cached data if the player is too far away from the game's master tile.
     */
    public static void clearStaleData() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        // Check distance for Game of Light data
        if (golSequence != null) {
            if (player.getDistanceSq(golX + 0.5, golY + 0.5, golZ + 0.5) > MAX_DISTANCE_SQ) {
                golSequence = null;
            }
        }

        // Check distance for Minesweeper data
        if (msBoard != null) {
            if (player.getDistanceSq(msX + 0.5, msY + 0.5, msZ + 0.5) > MAX_DISTANCE_SQ) {
                msBoard = null;
            }
        }
    }
}
