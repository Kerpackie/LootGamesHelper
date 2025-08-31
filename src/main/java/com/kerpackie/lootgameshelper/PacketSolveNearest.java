package com.kerpackie.lootgameshelper;

import net.minecraft.entity.player.EntityPlayerMP;

import com.kerpackie.lootgameshelper.commands.CommandSolveNearest;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSolveNearest implements IMessage {

    // This packet needs no data, its existence is the message.
    public PacketSolveNearest() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    // The handler runs on the server
    public static class Handler implements IMessageHandler<PacketSolveNearest, IMessage> {

        private final CommandSolveNearest command = new CommandSolveNearest();

        @Override
        public IMessage onMessage(PacketSolveNearest message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // A clever way to avoid duplicating code: we can execute the command's logic
            // directly on the server, acting on behalf of the player who sent the packet.
            command.processCommand(player, new String[0]); // Pass empty arguments.

            return null; // No reply packet is needed.
        }
    }
}
