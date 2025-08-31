package com.kerpackie.lootgameshelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketGoLData implements IMessage {

    public int[] sequence;
    public int x, y, z;

    public PacketGoLData() {} // Required default constructor

    public PacketGoLData(int[] sequence, int x, int y, int z) {
        this.sequence = sequence;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        int length = buf.readInt();
        this.sequence = new int[length];
        for (int i = 0; i < length; i++) {
            this.sequence[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(sequence.length);
        for (int j : sequence) {
            buf.writeInt(j);
        }
    }

    public static class Handler implements IMessageHandler<PacketGoLData, IMessage> {

        @Override
        public IMessage onMessage(PacketGoLData message, MessageContext ctx) {
            // On receiving the packet, update the client-side cache with the new data.
            ClientCache.updateGoLData(message.sequence, message.x, message.y, message.z);
            return null; // No reply packet
        }
    }
}
