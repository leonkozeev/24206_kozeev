package ru.nsu.torrent.protocol.pieces;


@FunctionalInterface
public interface PieceCompleteListener {
    void onPieceComplete(int pieceIndex);
}
