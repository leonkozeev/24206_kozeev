package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.KeepAliveMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public final class KeepAliveHandler implements MessageHandler<KeepAliveMessage> {
    @Override
    public void handle(KeepAliveMessage msg, PeerConnection peer) {
    }
}
