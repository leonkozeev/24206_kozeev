package ru.nsu.torrent.protocol.peer;


public interface PeerEventListener {
    default void onHandshakeComplete(PeerConnection peer) {}
    default void onDisconnected(PeerConnection peer) {}
    default void onWritePending(PeerConnection peer) {}

    void onBlockReceived(PeerConnection peer, int pieceIndex, int begin, byte[] block);
    void onServeRequest(PeerConnection peer, int pieceIndex, int begin, int length);
}
