package com.kerpackie.lootgameshelper;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kerpackie.lootgameshelper.commands.CommandGetData;
import com.kerpackie.lootgameshelper.commands.CommandScan;
import com.kerpackie.lootgameshelper.commands.CommandToggle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = LootGamesHelper.MODID, version = LootGamesHelper.VERSION, name = LootGamesHelper.NAME)
public class LootGamesHelper {

    public static final String MODID = "lootgameshelper";
    public static final String VERSION = "1.0";
    public static final String NAME = "LootGames Helper";

    public static SimpleNetworkWrapper network;
    public static final Logger logger = LogManager.getLogger(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger.info("Starting LootGames Helper Pre-Initialization...");
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        network.registerMessage(PacketGoLData.Handler.class, PacketGoLData.class, 0, Side.CLIENT);
        network.registerMessage(PacketMSData.Handler.class, PacketMSData.class, 1, Side.CLIENT);
        logger.info("Network and Packets Registered.");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Starting LootGames Helper Initialization...");

        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new ClientRenderHandler());
            ClientCommandHandler.instance.registerCommand(new CommandToggle());
        }
        logger.info("Event Handlers Registered.");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        logger.info("Registering server commands...");
        event.registerServerCommand(new CommandGetData());
        event.registerServerCommand(new CommandScan());
    }
}
