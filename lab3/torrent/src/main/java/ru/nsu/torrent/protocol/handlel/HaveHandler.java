package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.HaveMessage;
import ru.nsu.torrent.protocol.message.InterestedMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;

public final class HaveHandler implements MessageHandler<HaveMessage> {

    private final PieceManager pieceManager;

    public HaveHandler(PieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    @Override
    public void handle(HaveMessage msg, PeerConnection peer) {
        boolean[] bf = peer.bitfield();
        pieceManager.registerPeerHave(bf, msg.pieceIndex());
        if (!peer.state().amInterested() && pieceManager.peerHasAnythingWeNeed(bf)) {
            peer.state().onSendInterested();
            peer.send(InterestedMessage.INSTANCE);
        }
        peer.request();
    }
}
