package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record PieceMessage(int index, int begin, byte[] block) implements Message {
    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(13 + block.length);
        buf.putInt(9 + block.length).put((byte) 7).putInt(index).putInt(begin).put(block).flip();
        return buf;
    }
}
