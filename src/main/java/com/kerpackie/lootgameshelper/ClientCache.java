package com.kerpackie.lootgameshelper;

import net.minecraft.client.Minecraft;

public class ClientCache {

    private static final double MAX_DISTANCE = 32.0;

    // --- Global Overlay Toggle ---
    public static boolean isOverlayEnabled = true;

    // --- Game of Light Data ---
    public static int[] golSequence = null;
    public static int golX, golY, golZ;

    // --- Minesweeper Data ---
    public static byte[][] msBoard = null;
    public static int msX, msY, msZ, msSize, msAllocatedSize;

    public static void updateGoLData(int[] sequence, int x, int y, int z) {
        golSequence = sequence;
        golX = x;
        golY = y;
        golZ = z;
    }

    public static void updateMSData(byte[][] board, int x, int y, int z, int size, int allocatedSize) {
        msBoard = board;
        msX = x;
        msY = y;
        msZ = z;
        msSize = size;
        msAllocatedSize = allocatedSize;
    }

    public static void clearStaleData() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        if (golSequence != null) {
            if (mc.thePlayer.getDistance(golX, golY, golZ) > MAX_DISTANCE) {
                golSequence = null;
            }
        }
        if (msBoard != null) {
            if (mc.thePlayer.getDistance(msX, msY, msZ) > MAX_DISTANCE) {
                msBoard = null;
            }
        }
    }
}

