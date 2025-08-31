package com.kerpackie.lootgameshelper;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

public class Keybinds {

    // Define the keybind object
    public static KeyBinding solveNearestKey;

    public static void register() {
        // Create the keybind. The parameters are:
        // 1. Description shown in the controls menu
        // 2. Default key (K can be changed by the user)
        // 3. Category in the controls menu
        solveNearestKey = new KeyBinding("Solve Nearest Game", Keyboard.KEY_K, "LootGames Helper");

        // Register the keybind with Minecraft
        ClientRegistry.registerKeyBinding(solveNearestKey);
    }
}
