package com.kerpackie.lootgameshelper;

import net.minecraft.client.Minecraft;

public class ClientCache {

    private static final double MAX_DISTANCE = 32.0;

    // --- Global Overlay Toggle ---
    public static boolean isOverlayEnabled = true;

    // --- Game of Light Data ---
    public static int[] golSequence = null;
    public static int golX, golY, golZ;
    public static int currentGoLStep = 0; // Tracks the player's progress

    // --- Minesweeper Data ---
    public static byte[][] msBoard = null;
    public static int msX, msY, msZ, msSize, msAllocatedSize;

    public static void updateGoLData(int[] sequence, int x, int y, int z) {
        golSequence = sequence;
        golX = x;
        golY = y;
        golZ = z;
        currentGoLStep = 0; // Reset progress when a new sequence is received
    }

    public static void updateMSData(byte[][] board, int x, int y, int z, int size, int allocatedSize) {
        msBoard = board;
        msX = x;
        msY = y;
        msZ = z;
        msSize = size;
        msAllocatedSize = allocatedSize;
    }

    public static void advanceGoLStep() {
        if (golSequence != null) {
            currentGoLStep++;
            // If we've completed the sequence, loop back to the start for the next round
            if (currentGoLStep >= golSequence.length) {
                currentGoLStep = 0;
            }
        }
    }

    public static void clearStaleData() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        if (golSequence != null) {
            if (mc.thePlayer.getDistance(golX, golY, golZ) > MAX_DISTANCE) {
                golSequence = null;
                currentGoLStep = 0;
            }
        }
        if (msBoard != null) {
            if (mc.thePlayer.getDistance(msX, msY, msZ) > MAX_DISTANCE) {
                msBoard = null;
            }
        }
    }
}
