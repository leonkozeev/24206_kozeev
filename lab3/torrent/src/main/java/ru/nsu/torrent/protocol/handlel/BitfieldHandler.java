package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.BitfieldMessage;
import ru.nsu.torrent.protocol.message.InterestedMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;

public final class BitfieldHandler implements MessageHandler<BitfieldMessage> {

    private final PieceManager pieceManager;

    public BitfieldHandler(PieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    @Override
    public void handle(BitfieldMessage msg, PeerConnection peer) {
        pieceManager.registerPeerBitfield(peer.bitfield(), msg.bitfield());
        if (!peer.state().amInterested() && pieceManager.peerHasAnythingWeNeed(peer.bitfield())) {
            peer.state().onSendInterested();
            peer.send(InterestedMessage.INSTANCE);
        }
        peer.request();
    }
}
