package com.kerpackie.lootgameshelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        // Check if our custom key was just pressed
        if (Keybinds.solveNearestKey.isPressed()) {

            // Your requested condition: only send the solve request if the overlay is enabled
            // and a solution (either GoL or Minesweeper) is currently being displayed.
            if (ClientCache.golSequence != null || ClientCache.msBoard != null) {
                // If the conditions are met, send a packet to the server to run the command.
                LootGamesHelper.network.sendToServer(new PacketSolveNearest());
            }
        }
    }
}
