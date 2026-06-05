package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record HaveMessage(int pieceIndex) implements Message {
    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.putInt(5).put((byte) 4).putInt(pieceIndex).flip();
        return buf;
    }
}
