package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record InterestedMessage() implements Message {
    public static final InterestedMessage INSTANCE = new InterestedMessage();
    private static final byte[] BYTES = {0, 0, 0, 1, 2};

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(BYTES.clone());
    }
}
