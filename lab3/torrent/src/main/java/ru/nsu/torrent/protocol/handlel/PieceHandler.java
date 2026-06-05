package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.PieceMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public final class PieceHandler implements MessageHandler<PieceMessage> {
    @Override
    public void handle(PieceMessage msg, PeerConnection peer) {
        peer.pipeline().acknowledge(msg.index(), msg.begin());
        peer.listener().onBlockReceived(peer, msg.index(), msg.begin(), msg.block());
        peer.request();
    }
}
