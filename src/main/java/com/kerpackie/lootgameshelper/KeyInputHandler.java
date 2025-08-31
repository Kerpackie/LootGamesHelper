package com.kerpackie.lootgameshelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        // Check if our custom key was just pressed
        if (Keybinds.solveNearestKey.isPressed()) {
            LootGamesHelper.network.sendToServer(new PacketSolveNearest());
        }
    }
}
