package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record ChokeMessage() implements Message {
    public static final ChokeMessage INSTANCE = new ChokeMessage();
    private static final byte[] BYTES = {0, 0, 0, 1, 0};

    @Override
    public ByteBuffer encode() { return ByteBuffer.wrap(BYTES.clone()); }
}
