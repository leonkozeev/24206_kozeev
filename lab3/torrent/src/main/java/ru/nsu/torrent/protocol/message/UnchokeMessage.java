package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record UnchokeMessage() implements Message {
    public static final UnchokeMessage INSTANCE = new UnchokeMessage();
    private static final byte[] BYTES = {0, 0, 0, 1, 1};

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(BYTES.clone());
    }
}
