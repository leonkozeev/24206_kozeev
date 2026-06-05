package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record KeepAliveMessage() implements Message {
    public static final KeepAliveMessage INSTANCE = new KeepAliveMessage();
    private static final byte[] BYTES = {0, 0, 0, 0};

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(BYTES.clone());
    }
}
