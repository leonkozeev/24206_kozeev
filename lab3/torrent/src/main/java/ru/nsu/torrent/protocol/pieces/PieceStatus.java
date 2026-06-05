package ru.nsu.torrent.protocol.pieces;

public enum PieceStatus {
    MISSING,
    DOWNLOADING,
    VERIFYING,
    DONE
}
