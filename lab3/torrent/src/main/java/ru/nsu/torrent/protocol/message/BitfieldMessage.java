package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record BitfieldMessage(byte[] bitfield) implements Message {
    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(5 + bitfield.length);
        buf.putInt(1 + bitfield.length).put((byte) 5).put(bitfield).flip();
        return buf;
    }
}
