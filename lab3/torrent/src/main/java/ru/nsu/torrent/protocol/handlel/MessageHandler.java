package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.Message;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public interface MessageHandler<M extends Message> {
    void handle(M message, PeerConnection peer);
}
