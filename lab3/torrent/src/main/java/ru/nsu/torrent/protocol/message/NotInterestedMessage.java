package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record NotInterestedMessage() implements Message {
    public static final NotInterestedMessage INSTANCE = new NotInterestedMessage();
    private static final byte[] BYTES = {0, 0, 0, 1, 3};

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(BYTES.clone());
    }
}
