package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public record HandshakeMessage(byte[] infoHash, byte[] peerId) implements Message {
    private static final byte[] PSTR = "BitTorrent protocol".getBytes();

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(68);
        buf.put((byte) 19).put(PSTR).put(new byte[8]).put(infoHash).put(peerId);
        buf.flip();
        return buf;
    }
}
