package ru.nsu.torrent.protocol.pieces;


public record BlockRequest(int pieceIndex, int blockOffset, int blockLength) {
    public static final int BLOCK_SIZE = 16_384;
}
