package ru.nsu.torrent.network;

import java.nio.channels.SocketChannel;

public interface ConnectionHandler {
    void onAcceptable(SocketChannel sc);
    void onConnected(NetworkSession session);
    void onData(NetworkSession session);
    void onWriteReady(NetworkSession session);
    void onDisconnected(NetworkSession session);
}
