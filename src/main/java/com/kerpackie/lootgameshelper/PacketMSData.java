package com.kerpackie.lootgameshelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMSData implements IMessage {

    public byte[][] boardData;
    public int x, y, z, size;

    public PacketMSData() {} // Required default constructor

    public PacketMSData(byte[][] boardData, int x, int y, int z, int size) {
        this.boardData = boardData;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.size = buf.readInt();
        this.boardData = new byte[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.boardData[i][j] = buf.readByte();
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buf.writeByte(this.boardData[i][j]);
            }
        }
    }

    public static class Handler implements IMessageHandler<PacketMSData, IMessage> {

        @Override
        public IMessage onMessage(PacketMSData message, MessageContext ctx) {
            // Update the client cache with the minesweeper board data.
            ClientCache.updateMSData(message.boardData, message.x, message.y, message.z, message.size);
            return null; // No reply packet
        }
    }
}
