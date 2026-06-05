package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record RequestMessage(int index, int begin, int length) implements Message {
    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(17);
        buf.putInt(13).put((byte) 6).putInt(index).putInt(begin).putInt(length).flip();
        return buf;
    }
}
